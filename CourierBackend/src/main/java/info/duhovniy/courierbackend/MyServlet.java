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


public class MyServlet extends HttpServlet {
    private static Logger Log = Logger.getLogger("info.duhovniy.courierbackend.MyServlet");

    @Override
    public void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws IOException {
        resp.setContentType("text/plain");
        resp.getWriter().println("Please use the form to POST to this url");
    }

    @Override
    public void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws IOException {
        final String email = req.getParameter("name");
        if (email == null) {
            resp.getWriter().println("Please enter email");
            return;
        }

        Log.info("Sending the couriers list email.");

        String outString;
        outString = "<p>Sending the couriers list email.</p><p><strong>Note:</strong> ";
        outString = outString.concat("the servlet must be deployed to App Engine in order to ");
        outString = outString.concat("send the email. Running the server locally writes a message ");
        outString = outString.concat("to the log file instead of sending an email message.</p>");

        resp.getWriter().println(outString);

        // Note: Ensure that the [PRIVATE_KEY_FILENAME].json has read
        // permissions set.
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
                    todoText = todoText + " * " + childSnapshot.getValue().toString() + "\n";
                }

                // Now send the email

                // Note: When an application running in the development server calls the Mail
                // service to send an email message, the message is printed to the log.
                // The Java development server does not send the email message.

                // You can test the email without waiting for the cron job to run by
                // loading http://[FIREBASE_PROJECT_ID].appspot.com/send-email in your browser.

                Properties props = new Properties();
                Session session = Session.getDefaultInstance(props, null);
                try {
                    Message msg = new MimeMessage(session);
                    //Make sure you substitute your project-id in the email From field
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

                // Note: in a production application you should replace the hard-coded email address
                // above with code that populates msg.addRecipient with the app user's email address.
            }

            @Override
            public void onCancelled(DatabaseError error) {
                System.out.println("Error: " + error);
            }
        });
    }
}
