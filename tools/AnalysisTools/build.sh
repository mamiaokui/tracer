#!/bin/sh

gcc -Wall -g -o kernel-event-decompressor kernel-event-decompressor.c minilzo/minilzo.c 
gcc -Wall -g -o kernel-event-decoder kernel-event-decoder.c
gcc -Wall -g -o user-event-parser user-event-parser.c
