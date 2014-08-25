import sys
from subprocess import PIPE, Popen
from threading  import Thread
import time

try:
    from Queue import Queue, Empty
except ImportError:
    from queue import Queue, Empty  # python 3.x

ON_POSIX = 'posix' in sys.builtin_module_names

SERVER_STATUS = set(['STARTS', 'COLLECTING', 'IDLE', 'STOPS'])

class ThreadStatus:
    number = -1
    tpath = ""
    tfile = ""

def enqueue_output(out, queue):
    for line in iter(out.readline, b''):
        queue.put(line)
    out.close()


def start_server():
    p = Popen(["/bin/bash", "./run.sh"], cwd=r'../EventLoggingServer/', stdout=PIPE, bufsize=1, close_fds=ON_POSIX)
    q = Queue()
    t = Thread(target=enqueue_output, args=(p.stdout, q))
    t.daemon = True # thread dies with the program
    t.start()


def process_server():
    # read line without blocking
    while True:
        try:  line = q.get_nowait() # or q.get(timeout=.1)
        except Empty:
            time.sleep(2)
        else: # got line
            analyze_line(line)
            # ... do something with line

def analyze_line(line):
     
