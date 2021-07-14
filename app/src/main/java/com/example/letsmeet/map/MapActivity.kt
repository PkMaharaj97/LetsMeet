
package com.example.letsmeet.map
import android.Manifest
import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.PointF
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import android.widget.AdapterView.OnItemClickListener
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import com.example.letsmeet.R
import com.example.letsmeet.addevent.AddEvent
import com.google.android.material.card.MaterialCardView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.here.android.mpa.common.*
import com.here.android.mpa.common.PositioningManager.*
import com.here.android.mpa.guidance.NavigationManager
import com.here.android.mpa.mapping.*
import com.here.android.mpa.mapping.Map
import com.here.android.mpa.routing.*
import com.here.android.mpa.search.*
import java.io.File
import java.lang.ref.WeakReference
import java.util.*
import kotlin.collections.ArrayList


class MapActivity : FragmentActivity() {
    // map embedded in the map fragment
    private var map: Map? = null
    private lateinit var marker : MapMarker
    private lateinit var Srcmarker : MapMarker
    private lateinit var Dstmarker : MapMarker
    private lateinit var Usermarker : MapMarker

    private lateinit var mapRoute:MapRoute
    private var positioningManager: PositioningManager? = null
    private var positionListener:PositioningManager.OnPositionChangedListener? = null
    private lateinit var currentPosition:GeoCoordinate
    private lateinit var sourcePosition:GeoCoordinate
    private  var sourceName=""
    private  var sourceAddress=""
    private  var destinyName=""
    private  var destinyAddress=""
    private  var navigationMode=false

    private lateinit var destinyPosition:GeoCoordinate


    private var locBtn:FloatingActionButton?=null
    private var naviControlButton:FloatingActionButton? = null
    private var navigationManager: NavigationManager? = null
    private var geoBoundingBox: GeoBoundingBox? = null
    private var route: Route? = null
    private lateinit var  searchbar:MaterialCardView
    private lateinit var  navigationbar:MaterialCardView
    private lateinit var  togglebar:MaterialCardView
    private lateinit var  routeInfobar:MaterialCardView
    private lateinit var  enableSearchCard:ImageButton
    private lateinit var  enableNavCard:ImageButton
    private lateinit var  bikeMode:ImageButton
    private lateinit var  carMode:ImageButton
    private lateinit var  walkMode:ImageButton


    private var foregroundServiceStarted = false
    private var mapFragment: AndroidXMapFragment? = null
    private var duration:Int=0
    private var distance:Double=0.0

    private var autoSuggestAdapter: AutoSuggestAdapter? = null
    private var autoSuggests: ArrayList<AutoSuggest>? = null
    private var resultsListView: ListView? = null
    private var mapFragmentContainer: View? = null
    private var searchView: SearchView? = null
    private var source_searchView: SearchView? = null
    private var destiny_searchView: SearchView? = null
    private var tv_distance: TextView? = null
    private var tv_duration: TextView? = null
    private var tv_mode: TextView? = null

    private var searchListener:SearchListener? = null
    var discoverResultList: List<DiscoveryResult>? = null
    var transportMode:RouteOptions.TransportMode?=null
    private  var  menubtn:ImageButton?=null
    public override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_map)
        mapFragment=supportFragmentManager.findFragmentById(R.id.mapfragment) as AndroidXMapFragment?
        mapFragmentContainer =findViewById(R.id.mapfragment)
        searchListener = SearchListener()
        autoSuggests = ArrayList()
        checkPermissions()
        initVariables()
        addListerner()
        initAutoSuggestionControls()
    }
    private fun initVariables()
    {
        val sMarker= Image()
        sMarker.setImageResource(R.drawable.srcmarker)
        val dMarker= Image()
        dMarker.setImageResource(R.drawable.dstmarker)
        val mMarker= Image()
        mMarker.setImageResource(R.drawable.marker)
        val uMarker= Image()
        uMarker.setImageResource(R.drawable.homemarker)


        marker = MapMarker()
        marker.icon=mMarker
        Srcmarker = MapMarker()
        Srcmarker.icon=sMarker
        Dstmarker = MapMarker()
        Dstmarker.icon=dMarker

        Usermarker = MapMarker()
        Usermarker.icon=uMarker
        Usermarker.title="User"
        Usermarker.description="null"

        bikeMode=findViewById(R.id.mode_Bike)
        carMode=findViewById(R.id.mode_Car)
        walkMode=findViewById(R.id.mode_Walk)

        searchbar=findViewById(R.id.SearchCardView)
        routeInfobar=findViewById(R.id.route_info)
        tv_distance=findViewById(R.id.distance)
        tv_duration=findViewById(R.id.duration)
        tv_mode=findViewById(R.id.tv_mode)
        navigationbar=findViewById(R.id.NavCardView)
        togglebar=findViewById(R.id.toggleCardView)
        enableSearchCard=findViewById(R.id.open_search)
        enableNavCard=findViewById(R.id.open_nav)




    }
    fun addListerner()
    {
        enableSearchCard.setOnClickListener{
            navigationbar.visibility=View.INVISIBLE
            searchbar.visibility=View.VISIBLE }

        enableNavCard.setOnClickListener{
            searchbar.visibility=View.INVISIBLE
            navigationbar.visibility=View.VISIBLE }

        locBtn=findViewById(R.id.current_loc)

        locBtn?.setOnClickListener{goToUserLocation()}
        initNaviControlButton()


        menubtn=findViewById(R.id.select_menu)
        menubtn!!.setOnClickListener{createMenu()}

        bikeMode.setOnClickListener{setMode(bikeMode.id)}
        carMode.setOnClickListener{setMode(carMode.id)}
        walkMode.setOnClickListener{setMode(walkMode.id)}


    }

    @SuppressLint("RestrictedApi")
    private fun toggle():Boolean
    {
        if(togglebar.visibility==View.VISIBLE)
            togglebar.visibility=View.INVISIBLE
        else
            togglebar.visibility=View.VISIBLE


        if(locBtn!!.visibility==View.VISIBLE)
        {
            locBtn!!.visibility=View.INVISIBLE
            naviControlButton!!.visibility=View.INVISIBLE
        }
        else
        {
            locBtn!!.visibility=View.VISIBLE
            naviControlButton!!.visibility=View.VISIBLE
        }


        return false
    }

    @RequiresApi(Build.VERSION_CODES.JELLY_BEAN)
    private fun initialize() {

        // Set up disk cache path for the map service for this application
        val success: Boolean = MapSettings.setIsolatedDiskCacheRootPath(applicationContext.
        getExternalFilesDir(null).toString() + File.separator + ".here-maps")
        if (!success) {
            Toast.makeText(applicationContext, "Unable to set isolated disk cache path.", Toast.LENGTH_LONG).show()
        } else {
            mapFragment?.init { error ->
                if (error === OnEngineInitListener.Error.NONE) {
                    // retrieve a reference of the map from the map fragment
                    map = mapFragment?.map
                    map!!.isTrafficInfoVisible = true
                    map!!.mapScheme = Map.Scheme.NORMAL_TRAFFIC_DAY
                    transportMode=RouteOptions.TransportMode.SCOOTER
                    currentPosition= GeoCoordinate(15.3659661,76.1452926,1.073741824E9)
                    Usermarker.coordinate= GeoCoordinate(15.3659661,76.1452926,1.073741824E9)
                    map?.addMapObject(Usermarker)
                    sourcePosition=currentPosition
                    map!!.positionIndicator.isVisible = true
                    navigationManager = NavigationManager.getInstance()
                    ShowEventLocation()
                    mapFragment?.mapGesture?.addOnGestureListener(object : MapGesture.OnGestureListener.OnGestureListenerAdapter()
                    {
                        override fun onTapEvent(p0: PointF): Boolean
                        {
                            toggle()
                            return super.onTapEvent(p0)
                        }
                        override fun onLongPressEvent(p1: PointF): Boolean
                        {
                            val position = map!!.pixelToGeo(p1)!!
                            markerOnMapClick(position)
                            return false
                        }

                        @RequiresApi(Build.VERSION_CODES.O)
                        override fun onMapObjectsSelected(viewObjectList: MutableList<ViewObject>): Boolean
                        {
                            for (viewObject in viewObjectList) {
                                if (viewObject.baseType == ViewObject.Type.USER_OBJECT) {
                                    val mapObject = viewObject as MapObject
                                    if (mapObject.type == MapObject.Type.MARKER) {
                                        val selectedMarker = mapObject as MapMarker
                                        onMarkerClick(selectedMarker)
                                    }
                                    if (mapObject is MapRoute) {
                                        onRouteclick(mapObject)
                                        // other code
                                    }

                                }
                            }
                            return false

                        }
                    },0,false)
                    positioningManager = getInstance()
                    positionListener = object : OnPositionChangedListener {
                        override fun onPositionUpdated(method: LocationMethod?, position: GeoPosition?, isMapMatched: Boolean)
                        {
                            map!!.positionIndicator.isVisible = true
                            currentPosition = position?.coordinate!!
                            addUserMarker(currentPosition)
                        }

                        override fun onPositionFixChanged(method: LocationMethod, status: LocationStatus)
                        {
                            if (method == LocationMethod.GPS) {
                                var isExtrapolated:Boolean=((positioningManager!!.getRoadElement() != null) &&
                                        ((positioningManager!!.getRoadElement()!!.getAttributes()
                                            .contains(RoadElement.Attribute.TUNNEL))))
                                //  var hasGps:Boolean = status == LocationStatus!!.AVAILABLE


                            }

                        }
                    }
                    try {
                        positioningManager?.addListener(WeakReference(positionListener))
                        if (!positioningManager?.start(LocationMethod.GPS_NETWORK)!!) {
                            Log.e("HERE", "PositioningManager.start: Failed to " +
                                    "start...")
                        }
                    } catch (e: Exception) {
                        Log.e("HERE", "Caught: " + e.message)
                    }
                    map!!.positionIndicator.isVisible = true
                } else {

                    println("ERROR: Cannot initialize Map Fragment")
                    runOnUiThread {
                        AlertDialog.Builder(this@MapActivity).setMessage(
                            "Error : " + error.name+ "\n\n" + error.details
                        )
                            .setTitle(R.string.engine_init_error)
                            .setNegativeButton(android.R.string.cancel
                            ) { dialog, which -> finishAffinity() }
                            .create().show()
                    }
                }
            }
        }


    }


    fun setMode(id:Int)
    {
        if(id==R.id.mode_Bike)
        {
            bikeMode.setBackgroundResource(R.color.lightBlue)
            carMode.setBackgroundResource(R.color.white_greyish)
            walkMode.setBackgroundResource(R.color.white_greyish)

            map!!.removeAllMapObjects()
            transportMode=RouteOptions.TransportMode.SCOOTER
            if(sourcePosition.isValid &&destinyPosition.isValid)
                createRoute(sourcePosition,destinyPosition)
        }
        else if(id==R.id.mode_Car)
        {
          /*  bikeMode.setBackgroundResource(resources.getColor(R.color.white_greyish))
            carMode.setBackgroundResource(resources.getColor(R.color.lightBlue))
            walkMode.setBackgroundResource(resources.getColor(R.color.white_greyish))*/
            bikeMode.setBackgroundResource(R.color.white_greyish)
            carMode.setBackgroundResource(R.color.lightBlue)
            walkMode.setBackgroundResource(R.color.white_greyish)

            map!!.removeAllMapObjects()

            transportMode=RouteOptions.TransportMode.CAR
            if(sourcePosition.isValid &&destinyPosition.isValid)
                createRoute(sourcePosition,destinyPosition)
        }
        else if(id==R.id.mode_Walk)
        {
           /* bikeMode.setBackgroundResource(resources.getColor(R.color.white_greyish))
            carMode.setBackgroundResource(resources.getColor(R.color.white_greyish))
            walkMode.setBackgroundResource(resources.getColor(R.color.lightBlue))*/
            bikeMode.setBackgroundResource(R.color.white_greyish)
            carMode.setBackgroundResource(R.color.white_greyish)
            walkMode.setBackgroundResource(R.color.lightBlue)

            map!!.removeAllMapObjects()
            transportMode=RouteOptions.TransportMode.PEDESTRIAN
            if(sourcePosition.isValid &&destinyPosition.isValid)
                createRoute(sourcePosition,destinyPosition)
        }


    }

    fun createMenu()
    {
        val popup = PopupMenu(this@MapActivity, menubtn)
        popup.getMenuInflater().inflate(R.menu.mapbox_stylemenu, popup.getMenu())
        popup.setOnMenuItemClickListener { item ->
            when (item.itemId) {
                R.id.satellite -> {
                    map!!.mapScheme = Map.Scheme.HYBRID_DAY_TRANSIT
                    true
                }
                R.id.traffic -> {
                    map!!.mapScheme = Map.Scheme.NORMAL_TRAFFIC_DAY
                    true
                }
                R.id.night-> {
                    map!!.mapScheme = Map.Scheme.NORMAL_NIGHT
                    true
                }
                R.id.clear_map-> {
                    map!!.removeAllMapObjects()
                    navigationMode=false
                    true
                }


                else -> true
            }
        }

        popup.show()
    }


    /**
     * Checks the dynamically controlled permissions and requests missing permissions from end user.
     */
    private fun checkPermissions() {
        val missingPermissions: MutableList<String> = ArrayList()
        // check all required dynamic permissions
        for (permission in REQUIRED_SDK_PERMISSIONS) {
            val result = ContextCompat.checkSelfPermission(this, permission)
            if (result != PackageManager.PERMISSION_GRANTED) {
                missingPermissions.add(permission)
            }
        }

        if (missingPermissions.isNotEmpty()) {
            // request all missing permissions
            val permissions = missingPermissions
                .toTypedArray()
            ActivityCompat.requestPermissions(this, permissions, REQUEST_CODE_ASK_PERMISSIONS)
        } else {
            val grantResults = IntArray(REQUIRED_SDK_PERMISSIONS.size)
            Arrays.fill(grantResults, PackageManager.PERMISSION_GRANTED)
            onRequestPermissionsResult(REQUEST_CODE_ASK_PERMISSIONS, REQUIRED_SDK_PERMISSIONS, grantResults)
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray
    ) {
        when (requestCode) {
            REQUEST_CODE_ASK_PERMISSIONS -> {
                var index = permissions.size - 1
                while (index >= 0) {
                    if (grantResults[index] != PackageManager.PERMISSION_GRANTED) {
                        // exit the app if one permission is not granted
                        Toast.makeText(this, "Required permission '" + permissions[index] + "' not granted, exiting", Toast.LENGTH_LONG).show()
                        finish()
                        return
                    }
                    --index
                }
                // all permissions were granted
                initialize()
            }
        }
    }

    companion object {
        // permissions request code
        private const val REQUEST_CODE_ASK_PERMISSIONS = 1

        /**
         * Permissions that need to be explicitly requested from end user.
         */
        private val REQUIRED_SDK_PERMISSIONS = arrayOf(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.ACCESS_WIFI_STATE,
            Manifest.permission.ACCESS_COARSE_LOCATION
        )
    }



    fun markerOnMapClick(position: GeoCoordinate)
    {
        val request = ReverseGeocodeRequest(position)
        request.execute { data, error ->
            if (error != ErrorCode.NONE) {
                Log.e("Pkonmapclick", error.toString())
            } else {
                Log.e("whole", data.toString())

                val place = data?.address

                marker.coordinate = position
                marker.title="Dropped Marker"
                marker.description=place.toString()
                map?.addMapObject(marker)
                map?.setCenter(position, Map.Animation.NONE)
                map?.zoomLevel = map!!.maxZoomLevel//(map!!.maxZoomLevel + map!!.minZoomLevel)
            }
        }
    }
    fun onMarkerClick(placeMarker:MapMarker)
    {
        val isFromAddEvent=intent.getBooleanExtra("isfromAddevent",false)
        val   isFromReschedule=intent.getBooleanExtra("isFromReschedule",false)
        val locationFor=intent.getStringExtra("locationFor")

        val title=placeMarker.title
        val address=placeMarker.description
        val position=placeMarker.coordinate
        val buffer= StringBuffer()
        buffer.append("Address:$address\r\n")
        val builder = AlertDialog.Builder(this)
        builder.setCancelable(true)
        builder.setTitle(title)
        builder.setMessage(buffer.toString())
        if(isFromAddEvent||isFromReschedule){
            builder.setNegativeButton("Share Address", DialogInterface.OnClickListener { dialoginterface, i ->
                intent = Intent(this, AddEvent::class.java)
                intent.putExtra("islocation",true)
                intent.putExtra("placeName",title)
                intent.putExtra("placeAddress",address)
                intent.putExtra("long",position.longitude.toString())
                intent.putExtra("latt",position.latitude.toString())
                intent.putExtra("altt",position.altitude.toString())

                if( isFromAddEvent ) {
                    showToast("its new")
                    intent.putExtra("isNew", true)

                    intent.putExtra("contact",locationFor) }
                if( isFromReschedule ) {
                    showToast("its rescheduled")
                    intent.putExtra("isSchedule", true)
                }
                startActivity(intent)
            })
        }
        builder.show()

    }

    private fun goToUserLocation()
    {
        map?.setCenter(currentPosition, Map.Animation.BOW)
        map?.zoomLevel = (map!!.maxZoomLevel+map!!.minZoomLevel)/2

    }
    //<------------------------------------------Navigation Program --------------------------------------------------------->//
    private fun initNaviControlButton() {
        naviControlButton = findViewById(R.id.nav_fab)
        naviControlButton?.setOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View?)
            {
                when {
                    route == null -> {
                        try {
                            createRoute(sourcePosition, destinyPosition)
                        }catch (e:Exception) {
                            showToast("Targets Not Found")
                        }
                    }
                    navigationMode &&route!=null-> {
                        startNavigation()
                    }
                    else -> {
                        navigationMode=false
                       /** map!!.removeMapObject(mapRoute)
                        try {
                            createRoute(sourcePosition, destinyPosition)
                        }catch (e:Exception) {
                            showToast("Targets Not Found")
                        }**/

                         navigationManager!!.stop()

                        //Restore the map orientation to show entire route on screen
                      //  map?.zoomTo(geoBoundingBox!!, Map.Animation.NONE, 0f)
                        route=null

                    }
                }

            }
        })
    }

    @SuppressLint("SetTextI18n")
    private fun createRoute(Source:GeoCoordinate, Destiny:GeoCoordinate) {
        /* Initialize a CoreRouter */
        val coreRouter = CoreRouter()
        /* Initialize a RoutePlan */
        val routePlan = RoutePlan()
        val routeOptions = RouteOptions()
        Dstmarker.coordinate=Destiny
        Dstmarker.title=destinyName
        Dstmarker.description=destinyAddress
        map!!.addMapObject(Dstmarker)

        if(Source!=currentPosition){
        Srcmarker.coordinate=Source
            Srcmarker.title=sourceName
            Srcmarker.description=sourceAddress
            map!!.addMapObject(Srcmarker)}
        routeOptions.transportMode =transportMode
        /* Disable highway in this route. */routeOptions.setHighwaysAllowed(false)
        if(transportMode==RouteOptions.TransportMode.SCOOTER)
            routeOptions.routeType = RouteOptions.Type.FASTEST
        else
            routeOptions.routeType = RouteOptions.Type.BALANCED
        /* Calculate 1 route. */routeOptions.routeCount = 3
        /* Finally set the route option */routePlan.routeOptions = routeOptions

        /* Define waypoints for the route */
        /* START: 4350 Still Creek Dr */

        tv_mode!!.text="By: "+transportMode.toString()
        try {
            val startPoint = RouteWaypoint(Source)
            val destination = RouteWaypoint(Destiny)
            routePlan.addWaypoint(startPoint)
            routePlan.addWaypoint(destination)
        }
        catch (e:Exception )
        {Toast.makeText(this,"Coordinate not found",Toast.LENGTH_SHORT).show()

        }
        /* Add both waypoints to the route plan */

        /* Trigger the route calculation,results will be called back via the listener */
        coreRouter.calculateRoute(routePlan, object : Router.Listener<List<RouteResult>, RoutingError> {
            override fun onProgress(i: Int)
            {
                /* The calculation progress can be retrieved in this callback. */
            }

            @SuppressLint("SetTextI18n", "NewApi")
            override fun onCalculateRouteFinished(routeResults: List<RouteResult>?, routingError: RoutingError) {
                /* Calculation is done.Let's handle the result */
                if (routingError == RoutingError.NONE) {
                    if (routeResults?.get(0)?.route != null) {
                        var minDistance=1000.00
                        var minMapRoute=MapRoute(routeResults[0].route )
                        for(routeResult in routeResults)
                        {
                            route = routeResult.route

                            /* Create a MapRoute so that it can be placed on the map */
                            mapRoute = MapRoute(routeResult.route)
                         val Xdistance=(mapRoute.route!!.length/1000).toDouble()
                            if(Xdistance<minDistance)
                            {
                                minDistance=Xdistance
                                minMapRoute=mapRoute
                                                     }

                            /* Show the maneuver number on top of the route */
                               mapRoute.isManeuverNumberVisible = true
                            mapRoute.maneuverNumberColor=Color.YELLOW
                            mapRoute.color=generateColor()
                            mapRoute.renderType = MapRoute.RenderType.USER_DEFINED

                                     map!!.addMapObject(mapRoute)

                        }
                        minMapRoute.color=Color.DKGRAY
                        map!!.addMapObject(minMapRoute)
                        navigationMode=true
                        naviControlButton!!.setImageResource(R.drawable.navigation)
                    } else {
                        Toast.makeText(this@MapActivity, "Error:route results returned is not valid", Toast.LENGTH_LONG).show()
                    }
                } else {
                    Toast.makeText(this@MapActivity, "Error:route calculation returned error code: $routingError", Toast.LENGTH_LONG).show()
                }
            }


        })
    }


    @SuppressLint("NewApi")
    private fun startNavigation() {
        /* Configure Navigation manager to launch navigation on current map */
        navigationManager!!.setMap(map)
        /* Choose navigation modes between real time navigation and simulation */
        val alertDialogBuilder: AlertDialog.Builder = AlertDialog.Builder(this)
        alertDialogBuilder.setTitle("Route Created")
        alertDialogBuilder.setMessage("Choose Mode")
            alertDialogBuilder.setNegativeButton("Navigation", DialogInterface.OnClickListener { dialoginterface, i ->
                navigationManager!!.startNavigation(route!!)
                navigationMode=false
                map!!.tilt = 60F
                startForegroundService()
            })
        alertDialogBuilder.setPositiveButton("Simulation", DialogInterface.OnClickListener { dialoginterface, i ->
            navigationManager!!.simulate(route!!,60) //Simualtion speed is set to 60 m/s
            navigationMode=false
            map!!.tilt = 60F
            startForegroundService()
        })
        val alertDialog: AlertDialog = alertDialogBuilder.create()
        alertDialog.show()
        navigationManager!!.mapUpdateMode = NavigationManager.MapUpdateMode.ROADVIEW
        addNavigationListeners()
    }

    private fun startForegroundService() {
        if (!foregroundServiceStarted) {
            foregroundServiceStarted = true
            val startIntent = Intent(this, ForegroundService::class.java)
            startIntent.action = ForegroundService.START_ACTION
            this.applicationContext.startService(startIntent)
        }
    }

    private fun stopForegroundService() {
        if (foregroundServiceStarted) {
            foregroundServiceStarted = false
            val stopIntent = Intent(this, ForegroundService::class.java)
            stopIntent.action = ForegroundService.STOP_ACTION
            this.applicationContext.startService(stopIntent)
        }
    }

    private fun addNavigationListeners()
    {   navigationManager!!.addNavigationManagerEventListener(
        WeakReference<NavigationManager.NavigationManagerEventListener>(navigationManagerEventListener
        )
    )

        navigationManager!!.addPositionListener(
            WeakReference<NavigationManager.PositionListener>(m_positionListener)
        )
    }

    private val m_positionListener: NavigationManager.PositionListener = object : NavigationManager.PositionListener() {
        @SuppressLint("RestrictedApi")
        override fun onPositionUpdated(geoPosition: GeoPosition) {
            currentPosition = geoPosition.coordinate
            map!!.positionIndicator.isVisible = true
            addUserMarker(currentPosition)


        }
    }

    private val navigationManagerEventListener: NavigationManager.NavigationManagerEventListener =
        object : NavigationManager.NavigationManagerEventListener() {
            override fun onRunningStateChanged() {
                // Toast.makeText(applicationContext, "Running state changed", Toast.LENGTH_SHORT).show()
                Log.e("Here","Running state changed")

            }

            override fun onNavigationModeChanged() {
                Log.e("Here","Navigation mode changed")

                //Toast.makeText(applicationContext, "Navigation mode changed", Toast.LENGTH_SHORT).show()
            }

            override fun onEnded(navigationMode: NavigationManager.NavigationMode) {
                Toast.makeText(applicationContext, "$navigationMode was ended", Toast.LENGTH_SHORT).show()
                routeInfobar.visibility=View.GONE
                stopForegroundService()
            }

            override fun onMapUpdateModeChanged(mapUpdateMode: NavigationManager.MapUpdateMode) {
                // Toast.makeText(applicationContext, "Map update mode is changed to $mapUpdateMode", Toast.LENGTH_SHORT).show()
                Log.e("Here","Map update mode is changed to $mapUpdateMode")

            }

            override fun onRouteUpdated(route: Route)
            {

                Log.e("Here","Route updated")

                //  Toast.makeText(applicationContext, "Route updated", Toast.LENGTH_SHORT).show()
            }
            override fun onCountryInfo(s: String, s1: String) {
                // Toast.makeText(applicationContext, "Country info updated from $s to $s1", Toast.LENGTH_SHORT).show()
                Log.e("Here","Country info updated from $s to $s1")

            }
        }


    @SuppressLint("MissingSuperCall")
    override fun onDestroy() {
        if (navigationManager != null) {
            stopForegroundService()
            navigationManager!!.stop()
        }
    }
//<-------------------------------------------Auto Suggestion Starts Here--------------------------------------------------------------------->//

    private fun initAutoSuggestionControls() {
        searchView = findViewById(R.id.search)
        searchView!!.setOnClickListener{ searchView!!.setOnQueryTextListener(searchListener)
        }
        source_searchView = findViewById(R.id.source_search)
        source_searchView!!.setOnClickListener { source_searchView!!.setOnQueryTextListener(searchListener) }
        destiny_searchView = findViewById(R.id.destiny_search)
        destiny_searchView!!.setOnClickListener{ destiny_searchView!!.setOnQueryTextListener(searchListener) }
        searchView!!.setOnQueryTextListener(searchListener)
        source_searchView!!.setOnQueryTextListener(searchListener)
        destiny_searchView!!.setOnQueryTextListener(searchListener)

        resultsListView = findViewById(R.id.resultsListViev)
        autoSuggestAdapter = AutoSuggestAdapter(this, android.R.layout.simple_list_item_1, autoSuggests!!)
        resultsListView!!.adapter = autoSuggestAdapter
        resultsListView!!.onItemClickListener = OnItemClickListener { parent, view, position, id ->
            val item = parent.getItemAtPosition(position) as AutoSuggest

            if(source_searchView!!.hasFocus()){
                Toast.makeText(this,"it is source",Toast.LENGTH_SHORT).show()
                handleSelectedAutoSuggest(item,source_searchView!!)
            }
            else if(destiny_searchView!!.hasFocus()){
                Toast.makeText(this,"it is destiny",Toast.LENGTH_SHORT).show()
                handleSelectedAutoSuggest(item,destiny_searchView!!)
            }
            else if(searchView!!.hasFocus()){

                Toast.makeText(this,"it is search",Toast.LENGTH_SHORT).show()
                handleSelectedAutoSuggest(item,searchView!!)
            }


        }
    }


    private inner class SearchListener : SearchView.OnQueryTextListener
    {
        override fun onQueryTextSubmit(query: String): Boolean {
            return false
        }

        override fun onQueryTextChange(newText: String): Boolean {
            if (newText.isNotEmpty()) {
                doSearch(newText)
            } else {
                setSearchMode(false)
            }
            return false
        }

    }

    private fun doSearch(query: String) {
        setSearchMode(true)
        val textAutoSuggestionRequest = TextAutoSuggestionRequest(query)
        textAutoSuggestionRequest.setSearchCenter(map?.center!!)
        textAutoSuggestionRequest.execute(object : ResultListener<List<AutoSuggest>> {
            override fun onCompleted(autoSuggests: List<AutoSuggest>?, errorCode: ErrorCode
            ) {
                if (errorCode == ErrorCode.NONE) {
                    processSearchResults(autoSuggests!!)
                } else {
                    handleError(errorCode)
                }
            }
        })
    }

    private fun processSearchResults(AutoSuggests: List<AutoSuggest>) {
        this.runOnUiThread(Runnable {
            autoSuggests!!.clear()
            autoSuggests!!.addAll(AutoSuggests)
            autoSuggestAdapter!!.notifyDataSetChanged()
        })
    }

    private fun handleSelectedAutoSuggest(autoSuggest: AutoSuggest,minisearchview:SearchView) {
        when (autoSuggest.type) {
            AutoSuggest.Type.PLACE -> {
                try {
                    val autoSuggestPlace = autoSuggest as AutoSuggestPlace
                    val detailsRequest = autoSuggestPlace.placeDetailsRequest

                    detailsRequest!!.execute(object : ResultListener<Place> {
                        override fun onCompleted(place: Place?, errorCode: ErrorCode) {
                            if (errorCode == ErrorCode.NONE) {
                                setSearchMode(false)
                                minisearchview.setOnQueryTextListener(null)
                                minisearchview.setQuery(autoSuggestPlace.title, false)

                                if(minisearchview==searchView)
                                {
                                    map?.removeAllMapObjects()
                                    destinyPosition=place?.location?.coordinate!!
                                    sourcePosition=currentPosition
                                    marker.coordinate=destinyPosition
                                    destinyName=autoSuggest.title
                                    marker.title=destinyName
                                    destinyAddress=autoSuggest.vicinity
                                    marker.description=destinyAddress
                                    map?.addMapObject(marker)

                                    map?.setCenter(destinyPosition,Map.Animation.NONE)
                                    map?.zoomLevel = (map!!.maxZoomLevel+map!!.minZoomLevel)/2

                                }
                                else if(minisearchview==source_searchView)
                                {
                                    //map?.removeMapObjects(mutableListOf(Srcmarker,mapRoute))
                                    sourcePosition=place?.location?.coordinate!!
                                    Srcmarker.coordinate=sourcePosition
                                    sourceName=autoSuggest.title
                                    Srcmarker.title=sourceName
                                    sourceAddress=autoSuggest.vicinity
                                    Srcmarker.description=sourceAddress
                                    map?.addMapObject(Srcmarker)
                                    map?.setCenter(sourcePosition,Map.Animation.NONE)
                                    map?.zoomLevel = (map!!.maxZoomLevel+map!!.minZoomLevel)/2
                                }
                                else if(minisearchview==destiny_searchView)
                                {
                                    map?.removeMapObjects(mutableListOf(Dstmarker,mapRoute))
                                    destinyPosition=place?.location?.coordinate!!
                                    Dstmarker.coordinate=destinyPosition
                                    destinyName=autoSuggest.title
                                    Dstmarker.title=destinyName
                                    destinyAddress=autoSuggest.vicinity
                                    Dstmarker.description=destinyAddress
                                    map?.addMapObject(Dstmarker)
                                    map?.setCenter(destinyPosition,Map.Animation.NONE)
                                    map?.zoomLevel = (map!!.maxZoomLevel+map!!.minZoomLevel)/2
                                }
                                minisearchview.setOnQueryTextListener(searchListener)

                                // handlePlace(place!!)
                            } else {
                                handleError(errorCode)
                            }
                        }
                    })
                }catch (e1:Exception)
                {
                    showToast(e1.message!!)
                }

            }
            AutoSuggest.Type.SEARCH -> {
                val autoSuggestSearch = autoSuggest as AutoSuggestSearch
                val discoverRequest =
                    autoSuggestSearch.suggestedSearchRequest
                discoverRequest!!.execute(object : ResultListener<DiscoveryResultPage> {
                    override fun onCompleted(discoveryResultPage: DiscoveryResultPage?, errorCode: ErrorCode) {
                        if (errorCode == ErrorCode.NONE) {
                            discoverResultList = discoveryResultPage!!.items
                            //  val intent = Intent(this, ResultListActivity::class.java)
                            //  m_activity.startActivity(intent)
                        } else {
                            handleError(errorCode)
                        }
                    }
                })
            }
            AutoSuggest.Type.QUERY -> {
                val autoSuggestQuery = autoSuggest as AutoSuggestQuery
                val queryReqest = autoSuggestQuery.request
                queryReqest.execute(object : ResultListener<List<AutoSuggest>> {
                    override fun onCompleted(autoSuggests: List<AutoSuggest>?, errorCode: ErrorCode) {
                        if (errorCode == ErrorCode.NONE) {
                            processSearchResults(autoSuggests!!)
                            minisearchview.setOnQueryTextListener(null)
                            minisearchview.setQuery(autoSuggestQuery.queryCompletion, false)
                            minisearchview.setOnQueryTextListener(searchListener)
                        } else {
                            handleError(errorCode)
                        }
                    }
                })
            }
            AutoSuggest.Type.UNKNOWN -> {
            }
            else -> {
            }
        }
    }

    private fun setSearchMode(isSearch: Boolean) {
        if (isSearch) {
            mapFragmentContainer!!.visibility = View.INVISIBLE
            resultsListView!!.visibility=View.VISIBLE
            routeInfobar.visibility= View.INVISIBLE
        } else {
            mapFragmentContainer!!.visibility=View.VISIBLE
            resultsListView!!.visibility=View.INVISIBLE
        }
    }

    private fun handlePlace(place: Place) {
        val sb = StringBuilder()
        sb.append("Name: ").append(
            """
                ${place.name}
                
                """.trimIndent()
        )
        sb.append("Alternative name:").append(place.alternativeNames)
        showMessage("Place info", sb.toString(), false)
    }

    private fun handleError(errorCode: ErrorCode) {
        showMessage("Error", "Error description: " + errorCode.name, true)
    }

    private fun showMessage(title: String, message: String, isError: Boolean)
    {
        val builder = AlertDialog.Builder(this)
        builder.setTitle(title).setMessage(message)
        if (isError) {
            builder.setIcon(android.R.drawable.ic_dialog_alert)
        } else {
            builder.setIcon(android.R.drawable.ic_dialog_info)
        }
        builder.setNeutralButton("OK", null)

        builder.create().show()
    }
    private fun showToast(msg:String)
    {
        Toast.makeText(applicationContext,msg,Toast.LENGTH_SHORT).show()
    }


    fun ShowEventLocation()
    {
        val isFromViewEvent=intent.getBooleanExtra("isFromViewEvent",false)
        if(isFromViewEvent)
        {
            val title=intent.getStringExtra("Place")
            val description=intent.getStringExtra("Address")
            val longitude=intent.getDoubleExtra("Long",0.0)
            val latitude=intent.getDoubleExtra("Latt",0.0)
            val altitude=intent.getDoubleExtra("Altt",0.0)
            destinyPosition= GeoCoordinate(latitude,longitude,altitude)
            //  markerOnMapClick(destinyPosition)
            source_searchView!!.setQuery(Usermarker.description,false)
            destiny_searchView!!.setQuery(description,false)
            Dstmarker.coordinate=destinyPosition
            Dstmarker.title=title
            Dstmarker.description=description
            map?.addMapObject(Dstmarker)
            map?.zoomLevel=map!!.maxZoomLevel

        }

    }



    @RequiresApi(Build.VERSION_CODES.O)
    fun generateColor(): Int {
        val random = Random()
        return Color.argb( 255,random.nextInt(256), random.nextInt(256), random.nextInt(256))
    }
    @SuppressLint("SetTextI18n")
    fun onRouteclick(mapproute:MapRoute)
    {
        routeInfobar.visibility=View.VISIBLE
        mapRoute=mapproute
        route=mapproute.route
        duration=mapproute.route!!.getTtaExcludingTraffic(Route.WHOLE_ROUTE)!!.duration/60
        distance=(mapproute.route!!.length/1000).toDouble()
        tv_distance!!.text="Dst: $distance Kms"
        if(duration>60){
            val hrs=duration/60
            val min=duration%60
            tv_duration!!.text="Dur: $hrs Hrs.$min Mins"
        }else tv_duration!!.text="Dur: $duration Mins."

    }

    fun addUserMarker(position:GeoCoordinate)
    {
        val request = ReverseGeocodeRequest(position)
        request.execute { data, error ->
            if (error != ErrorCode.NONE) {
                Log.e("Pkonmapclick", error.toString())
            } else {
                Log.e("whole", data.toString())

                val place = data?.address

                Usermarker.coordinate = position
                Usermarker.title="User"
                Usermarker.description=place.toString()
                map?.addMapObject(Usermarker)
            }
        }


    }

}

