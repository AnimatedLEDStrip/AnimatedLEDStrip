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
import os
import threading
from concurrent.futures import as_completed, ThreadPoolExecutor

import imageio
import numpy
import tqdm
from PIL import Image, ImageDraw
from pygifsicle import optimize

base_dir = os.getcwd()


# https://stackoverflow.com/q/845058/1944087
def file_len(fname):
    with open(fname) as in_file:
        c = 0
        for c, l in enumerate(in_file):
            pass
    return c + 1


def set_point(draw: 'ImageDraw', x: int, y: int, r: int, g: int, b: int):
    draw.point((2 * x, 2 * y), fill=(r, g, b))
    draw.point((2 * x, 2 * y + 1), fill=(r, g, b))
    draw.point((2 * x + 1, 2 * y), fill=(r, g, b))
    draw.point((2 * x + 1, 2 * y + 1), fill=(r, g, b))


def create_gif(fname, height, width, numLEDs):
    try:
        thread_id = int(threading.current_thread().name.split('_')[-1]) + 1
    except ValueError:
        thread_id = 1

    renders = file_len(fname)

    file_name = fname[:-7]

    gif_out = imageio.get_writer(os.path.join(base_dir, "{}.gif".format(file_name)), format='GIF', mode='I', fps=50)

    with open(fname, 'r') as f:
        reader = csv.reader(f)
        with tqdm.tqdm(total=renders, desc=file_name,
                       position=thread_id, leave=False) as progress:
            i = 1
            for row in reader:
                image = Image.new('RGB', (width * 2, height * 2))
                drawing = ImageDraw.Draw(image)
                for v in range(0, numLEDs - 1):
                    set_point(draw=drawing,
                              x=locations[v][0],
                              y=locations[v][1],
                              r=int(row[3 * v]),
                              g=int(row[3 * v + 1]),
                              b=int(row[3 * v + 2]))
                gif_out.append_data(numpy.array(image))
                i = i + 1
                progress.update(1)

    gif_out.close()
    optimize("{}.gif".format(file_name), options=['-w'])


parser = argparse.ArgumentParser(description='Convert an AnimatedLEDStrip render log file into a GIF')
parser.add_argument('file', nargs='+', metavar='FILE', help='The file to convert')
parser.add_argument('--height', nargs=1, metavar='ROWS', type=int,
                    help='Manually set the height of the matrix', default=[100])
parser.add_argument('--width', nargs=1, metavar='COLUMNS', type=int,
                    help='Manually set the width of the matrix', default=[100])
parser.add_argument('--led-locations', nargs=1, metavar='FILE', type=str, help='File with LED locations', required=True)
parser.add_argument('--workers', nargs=1, metavar='#', type=int, help='Number of parallel jobs to execute',
                    default=[os.cpu_count()])

args = vars(parser.parse_args())
locations_file = args['led_locations'][0]
files = args['file']
height_arg = args['height'][0]
width_arg = args['width'][0]
numLEDs_calc = height_arg * width_arg
workers = args['workers'][0]

locations = []

with open(locations_file, 'r') as f:
    reader = csv.reader(f)
    for row in reader:
        locations = locations + [(int(float(row[0])), int(float(row[1])), int(float(row[2])))]

with tqdm.tqdm(total=len(files), desc='Processed 2D Anims', position=0) as file_progress:
    with ThreadPoolExecutor(max_workers=workers) as pool:
        futures = [pool.submit(create_gif, file, height_arg, width_arg, numLEDs_calc) for file in files]
        for future in as_completed(futures):
            file_progress.update(1)
