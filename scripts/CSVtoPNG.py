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

import tqdm
from PIL import Image, ImageDraw


# https://stackoverflow.com/q/845058/1944087
def file_len(fname):
    with open(fname) as f:
        c = 0
        for c, l in enumerate(f):
            pass
    return c + 1


def set_point(drawing: 'ImageDraw', x: int, y: int, r: int, g: int, b: int):
    drawing.point((x, y), fill=(r, g, b))


def get_height_by_render_count(render_count: int) -> int:
    if render_count > 30000:
        return 3000
    elif render_count > 10000:
        return 2000
    elif render_count > 2000:
        return 1000
    else:
        return render_count


parser = argparse.ArgumentParser(description='Convert an AnimatedLEDStrip render log file into a PNG')
parser.add_argument('file', nargs='+', metavar='FILE', help='The file to convert')
parser.add_argument('--height', nargs=1, metavar='ROWS', type=int, help='Manually set the height of the picture')
parser.add_argument('--num-leds', nargs=1, metavar='LEDS', type=int, help='Number of LEDs', default=240)

args = vars(parser.parse_args())
files = args['file']

for file in files:
    renders = file_len(file)
    numLEDs = args['num_leds']

    height = get_height_by_render_count(renders) if not args['height'] else args['height']
    columns = math.ceil(renders / height)
    width = columns * numLEDs + 10 * (columns - 1)

    multi_column = height != renders

    image = Image.new("RGB", (width, height))
    drawing = ImageDraw.Draw(image)

    with open(file, 'r') as f:
        reader = csv.reader(f)
        i = 0
        with tqdm.tqdm(total=renders, desc=file) as progress:
            for row in reader:
                for v in range(0, numLEDs - 1):
                    set_point(drawing=drawing,
                              x=(v + ((numLEDs + 10) * math.floor(i / height))),
                              y=(i % height),
                              r=int(row[3 * v]),
                              g=int(row[3 * v + 1]),
                              b=int(row[3 * v + 2]))
                if multi_column:
                    for v in range(0, 10):
                        set_point(drawing=drawing,
                                  x=(v + ((numLEDs + 10) * math.floor(i / height)) + numLEDs),
                                  y=(i % height),
                                  r=32, g=32, b=32)
                i += 1
                progress.update(1)

    image.save("{}.png".format(file[:-4]))
