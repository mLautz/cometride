package utdallas.ridetrackers.server;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import utdallas.ridetrackers.server.datatypes.CabStatus;
import utdallas.ridetrackers.server.datatypes.LatLng;
import utdallas.ridetrackers.server.datatypes.Route;
import utdallas.ridetrackers.server.datatypes.admin.RouteDetails;
import utdallas.ridetrackers.server.datatypes.admin.UserData;
import utdallas.ridetrackers.server.datatypes.driver.CabSession;
import utdallas.ridetrackers.server.datatypes.driver.TrackingUpdate;
import utdallas.ridetrackers.server.datatypes.rider.InterestedUpdate;
import utdallas.ridetrackers.server.db.CometRideDatabaseAccess;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Created with IntelliJ IDEA.
 * User: mlautz
 */
public class CometRideController {

    private final Logger logger = LoggerFactory.getLogger(CometRideController.class);

    // TODO: Get rid of these!!!
    private double testLat = 32.990709;
    private double testLng = -96.752627;
    private boolean markerIncrementing = true;
    // TODO: Get rid of these!!!

    private final CometRideDatabaseAccess db = new CometRideDatabaseAccess();


    //
    //  Routes
    //

    public Route[] getAllRoutes() {
        List<Route> routes = new ArrayList<Route>();

        // TODO: Get rid of test data!!!
        List<LatLng> testWaypoints = new ArrayList<LatLng>();
        testWaypoints.add( new LatLng( 32.985559, -96.749478 ) );
        testWaypoints.add( new LatLng( 32.985642, -96.749430 ) );
        testWaypoints.add( new LatLng( 32.991806, -96.753607 ) );
        testWaypoints.add( new LatLng( 32.985559, -96.749478 ) );

        List<LatLng> testSafepoints = new ArrayList<LatLng>();
        testSafepoints.add( new LatLng( 32.985559, -96.749478 ) );
        testSafepoints.add( new LatLng( 32.985642, -96.749430 ) );
        testSafepoints.add( new LatLng( 32.991806, -96.753607 ) );

        Route testRoute = new Route(
                "#900dba",
                "route1",
                "Route 1",
                "ACTIVE",
                testWaypoints,
                testSafepoints
        );

        routes.add( testRoute );

        List<LatLng> testWaypoints2 = new ArrayList<LatLng>();
        testWaypoints2.add( new LatLng( 32.990111, -96.743875 ) );
        testWaypoints2.add( new LatLng( 32.989424, -96.745462 ) );
        testWaypoints2.add( new LatLng( 32.991806, -96.753607 ) );
        testWaypoints2.add( new LatLng( 32.987391, -96.747009 ) );
        testWaypoints2.add( new LatLng( 32.987716, -96.746244 ) );
        testWaypoints2.add( new LatLng( 32.990111, -96.743875 ) );

        List<LatLng> testSafepoints2 = new ArrayList<LatLng>();
        testSafepoints2.add( new LatLng( 32.990111, -96.743875 ) );
        testSafepoints2.add( new LatLng( 32.989424, -96.745462 ) );
        testSafepoints2.add( new LatLng( 32.991806, -96.753607 ) );
        testSafepoints2.add( new LatLng( 32.987391, -96.747009 ) );
        testSafepoints2.add( new LatLng( 32.987716, -96.746244 ) );

        Route testRoute2 = new Route(
                "#edb712",
                "route2",
                "Route 2",
                "ACTIVE",
                testWaypoints2,
                testSafepoints2
        );

        routes.add( testRoute2 );
        // TODO: Get rid of test data!!!

        routes.addAll( db.getRouteDetails() );

        return routes.toArray( new Route[]{} );
    }

    public RouteDetails getRouteDetails( String id ) {
        RouteDetails details = new RouteDetails();
        details.setId( id );

        return details;
    }

    public String createRoute( RouteDetails newRouteDetails ) {

        String routeId = "route-" + UUID.randomUUID();
        newRouteDetails.setId( routeId );

        try {
            db.createRoute(newRouteDetails );
        } catch (Exception e) {
            logger.error( "Failed to create route in DB:\n" + e.getMessage() );
            // TODO: Throw error with condensed message
        }

        return routeId;
    }

    public String updateRoute( String id, RouteDetails createRoute ) {

        return id;
    }


    //
    //  Cabs
    //

    public CabStatus[] getAllCabStatuses( String queryType ) {
        List<CabStatus> cabs = new ArrayList<CabStatus>();

        // TODO: Get rid of test data
        if( markerIncrementing ) {
            this.testLat = this.testLat + 0.00005;
        } else {
            this.testLat = this.testLat - 0.00005;
        }

        if (this.testLat > 32.991771) {
            markerIncrementing = false;
        } else if (this.testLat < 32.990709) {
            markerIncrementing = true;
        }

        cabs.add( new CabStatus( "1", new LatLng( 32.987356, -96.746551 ), 8, 2, "route2", "ON_DUTY" ));
        cabs.add( new CabStatus( "2", new LatLng( this.testLat, this.testLng ), 8, 8, "route1", "ON_DUTY" ));
        // TODO: Get rid of test data

        try {
            cabs.addAll( db.retrieveCurrentCabStatuses() );
        } catch (SQLException e) {
            logger.error( "Failed to retrieve cab statuses from DB:\n" + e.getMessage() );
            // TODO: Throw error with condensed message
        }

        return cabs.toArray( new CabStatus[]{} );
    }

    public CabStatus getCabStatus( String id ) {
        CabStatus matchingStatus = null;

        try {
            matchingStatus = db.retrieveCabStatus( id );
        } catch (SQLException e) {
            logger.error( "Failed to retrieve cab status (" + id + ") from DB:\n" + e.getMessage() );
            // TODO: Throw error with condensed message
        }

        return matchingStatus;
    }


    //
    //  Driver
    //

    public String createCabSession( CabSession newCabSession) {
        // TODO: Maintain a list of session start / end times

        String sessionId = "cab-" + UUID.randomUUID();
        newCabSession.setCabSessionId( sessionId );

        try {
            db.createCabSession( newCabSession );
        } catch (Exception e) {
            logger.error( "Failed to create cab session in DB:\n" + e.getMessage() );
            // TODO: Throw error with condensed message
        }

        return sessionId;
    }

    public String updateDriverStatus( CabSession updatedCabSession) {

        return "1234";
    }

    public void storeTrackingUpdate( TrackingUpdate trackingUpdate) {
        // TODO: Validate input to ensure all properties are set
        db.persistTrackingUpdate(trackingUpdate);
    }


    //
    //  Rider
    //

    public void indicateRiderInterest( InterestedUpdate interestedUpdate ) {

    }


    //
    // Users
    //

    public UserData[] retrieveUsersData() {
        List<UserData> usersData = new ArrayList<UserData>();
        try {
            usersData.addAll( db.getUsersData() );
        } catch (Exception e) {
            logger.error( "Failed to retrieve users data from DB:\n" + e.getMessage() );
            // TODO: Throw error with condensed message
        }

        return usersData.toArray( new UserData[]{} );
    }

    public String createUser( UserData newData ) {
        try {
            db.createUser( newData );
        } catch (Exception e) {
            logger.error( "Failed to create user in DB:\n" + e.getMessage() );
            // TODO: Throw error with condensed message
        }

        return newData.getUserName();
    }

    public String updateUser( UserData data, String userName ) {
        if( data.getUserName().equals( userName ) ) {

            try {
                db.updateUser( data );
            } catch (Exception e) {
                logger.error( "Failed to update user in DB:\n" + e.getMessage() );
                // TODO: Throw error with condensed message
            }

            return userName;
        } else {
            throw new RuntimeException( "Provided user name does not match update data. Rejecting update!" );
        }
    }

    public void deleteUser( String userName ) {
        try {
            db.deleteUser( userName );
        } catch (Exception e) {
            logger.error( "Failed to delete user in DB:\n" + e.getMessage() );
            // TODO: Throw error with condensed message
        }
    }
}