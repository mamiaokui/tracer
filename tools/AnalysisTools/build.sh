#!/bin/sh

gcc -Wall -O3 -o kernel-event-decompressor kernel-event-decompressor.c minilzo/minilzo.c 
gcc -Wall -O3 -o kernel-event-decoder kernel-event-decoder.c
gcc -Wall -O3 -o user-event-parser user-event-parser.c
