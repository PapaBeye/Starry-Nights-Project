from http.server import HTTPServer, BaseHTTPRequestHandler

class MyServ(BaseHTTPRequestHandler):
    def do_GET(self):
        if self.path == '/':
            self.path = '/index.html'
        try:
            f =  open(self.path[1:]).read()
            self.send_response(200)
        except Exception as e:
            f = str(e)
            self.send_response(404)
        self.end_headers()
        self.wfile.write(bytes(f, 'utf-8'))

httpd = HTTPServer(("localhost",80), MyServ)
httpd.serve_forever()

