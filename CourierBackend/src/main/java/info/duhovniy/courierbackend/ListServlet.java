package info.duhovniy.courierbackend;

import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.util.logging.Logger;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


public class ListServlet extends HttpServlet {
    private static Logger Log = Logger.getLogger("info.duhovniy.courierbackend.ListServlet");
    private static CourierRepo repo = new CourierRepo();

    @Override
    public void doGet(final HttpServletRequest req, HttpServletResponse resp)
            throws IOException {
        resp.getWriter().println("Preparing couriers list");

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

                repo.clear();
                for (DataSnapshot childSnapshot : dataSnapshot.getChildren())
                    repo.createCourier(childSnapshot.getValue(Courier.class));
            }

            @Override
            public void onCancelled(DatabaseError error) {
                System.out.println("Error: " + error);
            }
        });
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        if (!repo.isEmpty()) {
            req.setAttribute("list", repo.getAllCouriers());
            try {
                // start list rendering through JSP
                getServletContext().getRequestDispatcher("/pages/courier-list.jsp").include(req, resp);
            } catch (ServletException e) {
                Log.info("Servlet exception");
            } catch (IOException e) {
                Log.info("IO exception");
            }
        } else
            resp.getWriter().println("List not ready yet! Please first do the Refresh");
    }
}
