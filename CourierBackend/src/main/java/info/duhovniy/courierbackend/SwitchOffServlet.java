package info.duhovniy.courierbackend;

import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.IOException;
import java.util.logging.Logger;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


public class SwitchOffServlet extends HttpServlet {
    private static Logger Log = Logger.getLogger("info.duhovniy.courierbackend.SwitchOffServlet");

    @Override
    public void doGet(final HttpServletRequest req, HttpServletResponse resp)
            throws IOException {
        final String id = req.getParameter("id");
        resp.getWriter().println("Switching Off Courier - " + id);

        FirebaseOptions options = new FirebaseOptions.Builder()
                .setServiceAccount(getServletContext().getResourceAsStream("/WEB-INF/CourierApp-0569e878205c.json"))
                .setDatabaseUrl("https://courierapp-59940.firebaseio.com/")
                .build();

        try {
            FirebaseApp.getInstance();
        } catch (Exception error) {
            Log.info("doesn't exist...");
        }

        try {
            FirebaseApp.initializeApp(options);
        } catch (Exception error) {
            Log.info("already exists...");
        }

        // As an admin, the app has access to read and write all data, regardless of Security Rules
        DatabaseReference ref = FirebaseDatabase
                .getInstance()
                .getReference();

        ref.child(id).child("on").setValue(false);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        doGet(req, resp);
    }
}