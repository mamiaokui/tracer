import os
import sys
import re
import shutil
from subprocess import PIPE, Popen
from threading  import Thread
import time

try:
    from Queue import Queue, Empty
except ImportError:
    from queue import Queue, Empty  # python 3.x

ON_POSIX = 'posix' in sys.builtin_module_names
q = Queue()
SERVER_STATUS = set(['STARTS', 'COLLECTING', 'IDLE', 'STOPS'])
current_status = 'IDLE'
previous_status = 'IDLE'

def enqueue_output(out, queue):
    for line in iter(out.readline, b''):
        queue.put(line)
    out.close()

def start_server():
    p = Popen(["/bin/bash", "./run.sh"], cwd=r'../EventLoggingServer/', stdout=PIPE, bufsize=1, close_fds=ON_POSIX)
    t = Thread(target=enqueue_output, args=(p.stdout, q))
    t.daemon = True # thread dies with the program
    t.start()


def process_server():
    global current_status
    global previous_status

    started_threads = []
    current_dir = "NULL"
    # read line without blocking
    while True:
        try:  line = q.get_nowait() # or q.get(timeout=.1)
        except Empty:
            time.sleep(4)
            #if we are in idle for more than 4 secs (previous state is also IDLE)
            #we are going to fire the raw data processing
            print current_dir
            print previous_status
            print current_status
            if (previous_status == 'IDLE' and current_status == 'IDLE'
		and current_dir != 'NULL'):
                #First we move all the current_dir to a tmp_dir
                shutil.rmtree("/tmp/"+current_dir, True)
                shutil.move(os.getcwd()+"/../EventLoggingServer/"+current_dir, "/tmp/")
                #start preprocess.sh
                p = Popen(["/bin/bash", "./preprocess.sh", "/tmp/"+current_dir], cwd=r'../AnalysisTools/')
                p.wait()

                #start split.sh
                p = Popen(["/bin/bash", "./split.sh"], cwd=r'../AnalysisTools/')
                p.wait()
                #start user trace analysis
                p = Popen(["/bin/bash", "./analyze.sh"], cwd=r'../AnalysisTools/user_trace_analysis/')
                p.wait()
                #generate the trasactions.txt
                p = Popen(["/bin/bash", "./genreport.sh"], cwd=r'../AnalysisTools/user_trace_analysis/')
                p.wait()

                #move the transactions.txt to webserver dir
                shutil.move(os.getcwd()+"/../AnalysisTools/user_trace_analysis/transactions.txt", os.getcwd()+"/" + time.strftime("%Y%m%d-%H%M%S")+"-transactions.txt")
                #clear current_dir
                current_dir = "NULL"

            previous_status = current_status
        else: # got line
            current_dir = analyze_line(line, started_threads, current_dir)
            # ... do something with line

#IDEA: Once there is threads started, wait all the threads finished and wait more 4 sec 
#If there is no threads started again then get into 
LINE_PARSER = "<Thread (\d+)> (.) (\S+)"

def analyze_line(line, started_threads, current_dir):
    global current_status

    result = re.compile(LINE_PARSER).split(line)
    if ( len(result) != 5):
        return current_dir

    #print result 
    if result[2] == 'S':
        return os.path.dirname(result[3])

    if result[2] == 'B':
        started_threads.append(result[1])
        #print started_threads
        current_status = 'COLLECTING'

    if result[2] == 'E':
        #print started_threads
        started_threads.remove(result[1])
        if len(started_threads) == 0:
            current_status = 'IDLE'

    return current_dir

def main():
    start_server()
    process_server()

if __name__ == "__main__":
    main()
