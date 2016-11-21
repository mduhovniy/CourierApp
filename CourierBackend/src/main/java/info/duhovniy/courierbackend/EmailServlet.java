/*
   For step-by-step instructions on connecting your Android application to this backend module,
   see "App Engine Java Servlet Module" template documentation at
   https://github.com/GoogleCloudPlatform/gradle-appengine-templates/tree/master/HelloWorld
*/

package info.duhovniy.courierbackend;

import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Properties;
import java.util.logging.Logger;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


public class EmailServlet extends HttpServlet {
    private static Logger Log = Logger.getLogger("info.duhovniy.courierbackend.EmailServlet");

    @Override
    public void doGet(final HttpServletRequest req, final HttpServletResponse resp)
            throws IOException {
        resp.getWriter().println("This page you can not call directly!");
    }

    @Override
    public void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws IOException {
        final String email = req.getParameter("name");
        if (email == null) {
            resp.getWriter().println("Please enter email");
            return;
        }

        resp.getWriter().println("Sending the couriers list email.");

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

        // This fires when the servlet first runs, returning all the existing values
        // only runs once, until the servlet starts up again.
        ref.addListenerForSingleValueEvent(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Object document = dataSnapshot.getValue();
                Log.info("new value: " + document);

                String todoText = "Couriers List\n\n";

                for (DataSnapshot childSnapshot : dataSnapshot.getChildren()) {
                    todoText += " * " + childSnapshot.getValue().toString() + "\n";
                }

                Properties props = new Properties();
                Session session = Session.getDefaultInstance(props, null);
                try {
                    Message msg = new MimeMessage(session);
                    msg.setFrom(new InternetAddress("reminder@courierapp-59940.appspotmail.com",
                            "Couriers List"));
                    msg.addRecipient(Message.RecipientType.TO,
                            new InternetAddress(email, "Recipient"));
                    msg.setSubject("Couriers List...");
                    msg.setText(todoText);
                    Transport.send(msg);
                } catch (MessagingException | UnsupportedEncodingException e) {
                    Log.warning(e.getMessage());
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                System.out.println("Data Base Error: " + error);
            }
        });
    }
}
