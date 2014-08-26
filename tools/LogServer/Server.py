import time
import BaseHTTPServer
import re
from datetime import datetime

HOST_NAME = '10.33.43.6' # !!!REMEMBER TO CHANGE THIS!!!
PORT_NUMBER = 8080 # Maybe set this to 9000.

class Event:
    time = "null"
    pid = 0
    delay = 5

class MyHandler(BaseHTTPServer.BaseHTTPRequestHandler):
    def do_HEAD(s):
        s.send_response(200)
        s.send_header("Content-type", "text/html")
        s.end_headers()
    def do_GET(s):
        """Respond to a GET request."""
        s.send_response(200)
        s.send_header("Content-type", "text/html")
        s.end_headers()
        #Process transactions.txt
        transactions_list = []
        status = 0
        e = Event()
        for line in open("transactions.txt"):
            line = line.strip()
            if (status == 0):
                e.pid = re.split("(\S+)-(\d+)-(\S+)", line)[2]
                status = 1
                continue
            if (status == 1):
                e.time = datetime.strptime(line[1:26], "%Y-%m-%d %H:%M:%S.%f")
                status = 2
                continue
            if (status == 2):
                if (line[0:11] != "Transaction"):
                    continue
                if "." in line[17:]:
                    e.delay = datetime.strptime(line[17:], "%H:%M:%S.%f")
                else:
                    e.delay = datetime.strptime(line[17:], "%H:%M:%S")
                transactions_list.append(e)
                e = Event()
                status = 0

        epoch = datetime(1900, 1, 1, 0, 0, 0)
        pid_list = ["Time", ]
        timeline = []
        timeline_list = []
        sorted_transactions_list = sorted(transactions_list, key=lambda x: time.mktime(x.time.timetuple()))
        for event in sorted_transactions_list:
           if event.pid not in pid_list:
               pid_list.append(event.pid)

        for event in sorted_transactions_list:
           timeline = [0]*(len(pid_list))
           timeline[0] = str(event.time)
           delta = event.delay - epoch
           timeline[pid_list.index(event.pid)] = delta.total_seconds() * 1000.0 
           timeline_list.append(timeline)

        for content in open("main.html"):
            if (content.strip() == "HEAD"):
                s.wfile.write(str(pid_list)+",")
            elif (content.strip() == "BODY"):
                s.wfile.write(str(timeline_list)[1:-1]) 
            else:
                s.wfile.write(content)

if __name__ == '__main__':
    server_class = BaseHTTPServer.HTTPServer
    httpd = server_class((HOST_NAME, PORT_NUMBER), MyHandler)
    print time.asctime(), "Server Starts - %s:%s" % (HOST_NAME, PORT_NUMBER)
    try:
        httpd.serve_forever()
    except KeyboardInterrupt:
        pass
    httpd.server_close()
    print time.asctime(), "Server Stops - %s:%s" % (HOST_NAME, PORT_NUMBER)
