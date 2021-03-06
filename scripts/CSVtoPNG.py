#!/usr/bin/env python3

#  Copyright (c) 2018-2021 AnimatedLEDStrip
#
#  Permission is hereby granted, free of charge, to any person obtaining a copy
#  of this software and associated documentation files (the "Software"), to deal
#  in the Software without restriction, including without limitation the rights
#  to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
#  copies of the Software, and to permit persons to whom the Software is
#  furnished to do so, subject to the following conditions:
#
#  The above copyright notice and this permission notice shall be included in
#  all copies or substantial portions of the Software.
#
#  THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
#  IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
#  FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
#  AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
#  LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
#  OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
#  THE SOFTWARE.

import argparse
import csv
import math
import os
import threading
from concurrent.futures import as_completed, ThreadPoolExecutor

import tqdm
from PIL import Image, ImageDraw


# https://stackoverflow.com/q/845058/1944087
def file_len(fname):
    with open(fname) as in_file:
        c = 0
        for c, l in enumerate(in_file):
            pass
    return c + 1


def set_point(draw: 'ImageDraw', x: int, y: int, r: int, g: int, b: int):
    draw.point((x, y), fill=(r, g, b))


def get_height_by_render_count(render_count: int) -> int:
    if render_count > 30000:
        return 3000
    elif render_count > 10000:
        return 2000
    elif render_count > 2000:
        return 1000
    else:
        return render_count


def create_png(fname):
    try:
        thread_id = int(threading.current_thread().name.split('_')[-1]) + 1
    except ValueError:
        thread_id = 1

    renders = file_len(fname)
    file_name = fname[:-7]

    num_leds = args['num_leds'][0]

    height = get_height_by_render_count(renders) if not args['height'] else args['height']
    columns = math.ceil(renders / height)
    width = columns * num_leds + 10 * (columns - 1)

    multi_column = height != renders

    image = Image.new('RGB', (width, height))
    drawing = ImageDraw.Draw(image)

    with open(fname, 'r') as f:
        reader = csv.reader(f)
        i = 0
        with tqdm.tqdm(total=renders, desc=file_name,
                       position=thread_id, leave=False) as progress:
            for row in reader:
                for v in range(0, num_leds - 1):
                    set_point(draw=drawing,
                              x=(v + ((num_leds + 10) * math.floor(i / height))),
                              y=(i % height),
                              r=int(row[3 * v]),
                              g=int(row[3 * v + 1]),
                              b=int(row[3 * v + 2]))
                if multi_column:
                    for v in range(0, 10):
                        set_point(draw=drawing,
                                  x=(v + ((num_leds + 10) * math.floor(i / height)) + num_leds),
                                  y=(i % height),
                                  r=32, g=32, b=32)
                i += 1
                progress.update(1)

    image.save("{}.png".format(file_name))


parser = argparse.ArgumentParser(description='Convert an AnimatedLEDStrip render log file into a PNG')
parser.add_argument('file', nargs='+', metavar='FILE', help='The file to convert')
parser.add_argument('--height', nargs=1, metavar='ROWS', type=int, help='Manually set the height of the picture')
parser.add_argument('--num-leds', nargs=1, metavar='LEDS', type=int, help='Number of LEDs', default=[240])
parser.add_argument('--workers', nargs=1, metavar='#', type=int, help='Number of parallel jobs to execute',
                    default=[os.cpu_count()])

args = vars(parser.parse_args())
files = args['file']
workers = args['workers'][0]

with tqdm.tqdm(total=len(files), desc='Processed 1D Anims', position=0) as file_progress:
    with ThreadPoolExecutor(max_workers=workers) as pool:
        futures = [pool.submit(create_png, file) for file in files]
        for future in as_completed(futures):
            file_progress.update(1)
