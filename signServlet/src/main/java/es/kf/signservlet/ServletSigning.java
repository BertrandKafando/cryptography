package es.kf.signservlet;

import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.Part;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileItemFactory;
import org.apache.commons.fileupload.RequestContext;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.GeneralSecurityException;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.cert.Certificate;
import java.util.Arrays;
import java.util.List;

@WebServlet(name = "sign", value = "/sign")
@MultipartConfig(
        fileSizeThreshold = 1024 * 1024, // 1MB
        maxFileSize = 1024 * 1024 * 4,   // 4MB
        maxRequestSize = 1024 * 1024 * 4 // 4MB
)
public class ServletSigning extends HttpServlet {

    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        String keystorepath = getServletConfig().getInitParameter("keystorepath");
        String keystoretype = getServletConfig().getInitParameter("keystoretype");
        String alias = getServletConfig().getInitParameter("alias");
        String storepass = getServletConfig().getInitParameter("storepass");
        String keypass = getServletConfig().getInitParameter("keypass");
        try {
            System.out.println(keystorepath);
            InputStream is = getServletContext().getResourceAsStream(keystorepath);
            KeyStore ks = KeyStore.getInstance(KeyStore.getDefaultType());
            ks.load(is, storepass.toCharArray());
            PrivateKey pk = (PrivateKey) ks.getKey("myalias", "mypassword".toCharArray());
            Certificate cert = ks.getCertificate("mycert");
            Certificate[] chain = new Certificate[] { cert };
            SignImp signImp = new SignImp(pk, chain);
            config.getServletContext().setAttribute("signImp", signImp);

        } catch (GeneralSecurityException | IOException e) {
            throw new ServletException(e);
        }


    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.getRequestDispatcher("SignServlet.jsp").forward(request, response);

    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setHeader("Expires", "0");
        resp.setHeader("Cache-Control",
                "must-revalidate, post-check=0, pre-check=0");
        resp.setHeader("Pragma", "public");
        resp.setContentType("application/pdf");
        OutputStream os = resp.getOutputStream();
        try {
            InputStream is = null;
            String reason = req.getParameter("reason");
            String location = req.getParameter("location");
            Part filePart = req.getPart("pdf");
            is = filePart.getInputStream();
            SignImp signImp =
                    (SignImp) getServletContext().getAttribute("signImp");
            byte[] result = signImp.signDoc(is, reason, location);
            resp.setContentLength(result.length);
            for (byte b : result) {
                os.write(b);
            }
        } catch (Exception e) {
            throw new ServletException(e);
        }
        os.flush();
        os.close();
    }

}
