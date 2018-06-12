package zero.ucamaps;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DialogFragment;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.ViewGroup.MarginLayoutParams;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.esri.android.map.GraphicsLayer;
import com.esri.android.map.LocationDisplayManager;
import com.esri.android.map.LocationDisplayManager.AutoPanMode;
import com.esri.android.map.MapOnTouchListener;
import com.esri.android.map.MapView;
import com.esri.android.map.event.OnPinchListener;
import com.esri.android.map.event.OnStatusChangedListener;

import zero.ucamaps.beans.MapPoint;
import zero.ucamaps.database.CargaDetalles;
import zero.ucamaps.database.Constantes;
import zero.ucamaps.database.RutaEspecial;
import zero.ucamaps.database.Sitio;
import zero.ucamaps.database.volleySingleton;
import zero.ucamaps.dialogs.DialogFavoriteRoute;
import zero.ucamaps.dialogs.DialogInfoRoutes;
import zero.ucamaps.dialogs.DialogSaveNote;
import zero.ucamaps.dialogs.ProgressDialogFragment;
import zero.ucamaps.location.DirectionsDialogFragment;
import zero.ucamaps.location.DirectionsDialogFragment.DirectionsDialogListener;
import zero.ucamaps.location.RoutingDialogFragment;
import zero.ucamaps.location.RoutingDialogFragment.RoutingDialogListener;
import zero.ucamaps.tools.Compass;
import zero.ucamaps.tts.TTSManager;
import zero.ucamaps.util.GlobalPoints;
import zero.ucamaps.util.TaskExecutor;

import com.esri.android.runtime.ArcGISRuntime;
import com.esri.core.geometry.Envelope;
import com.esri.core.geometry.GeometryEngine;
import com.esri.core.geometry.LinearUnit;
import com.esri.core.geometry.Point;
import com.esri.core.geometry.Polyline;
import com.esri.core.geometry.SpatialReference;
import com.esri.core.geometry.Unit;
import com.esri.core.map.Graphic;
import com.esri.core.portal.BaseMap;
import com.esri.core.portal.Portal;
import com.esri.core.portal.WebMap;
import com.esri.core.symbol.FontDecoration;
import com.esri.core.symbol.FontStyle;
import com.esri.core.symbol.FontWeight;
import com.esri.core.symbol.PictureMarkerSymbol;
import com.esri.core.symbol.SimpleLineSymbol;
import com.esri.core.symbol.SimpleLineSymbol.STYLE;
import com.esri.core.symbol.TextSymbol;
import com.esri.core.tasks.geocode.Locator;
import com.esri.core.tasks.geocode.LocatorFindParameters;
import com.esri.core.tasks.geocode.LocatorGeocodeResult;
import com.esri.core.tasks.geocode.LocatorReverseGeocodeResult;
import com.esri.core.tasks.na.NAFeaturesAsFeature;
import com.esri.core.tasks.na.Route;
import com.esri.core.tasks.na.RouteDirection;
import com.esri.core.tasks.na.RouteParameters;
import com.esri.core.tasks.na.RouteResult;
import com.esri.core.tasks.na.RouteTask;
import com.esri.core.tasks.na.StopGraphic;

import android.view.ViewTreeObserver.OnGlobalLayoutListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Implements the view that shows the map.
 */
public class MapFragment extends Fragment implements RoutingDialogListener, OnCancelListener {
	public static final String TAG = MapFragment.class.getSimpleName();

	private volleySingleton volley;
	private RequestQueue requestQueue;

	public int contadorGpsAlert = 0;
	private static final String KEY_BASEMAP_ITEM = "KEY_BASEMAP_ITEM";
    private static final String KEY_SOUND_ITEM = "KEY_SOUND_ITEM";
	private static final String KEY_IS_LOCATION_TRACKING = "IsLocationTracking";
	private static final int REQUEST_CODE_PROGRESS_DIALOG = 1;
	private static final String SEARCH_HINT = "Busqueda";

	private static FrameLayout.LayoutParams mlayoutParams;

    private static boolean TheresAPlace = false;

	// Margins parameters for search view
	private static int TOP_MARGIN_SEARCH = 55;
	private static int LEFT_MARGIN_SEARCH = 15;
	private static int RIGHT_MARGIN_SEARCH = 15;
	private static int BOTTOM_MARGIN_SEARCH = 0;

	// Margin parameters for compass
	private static int TOP_MARGIN_COMPASS = 15;
	private static int LEFT_MARGIN_COMPASS = 0;
	private static int BOTTOM_MARGIN_COMPASS = 0;
	private static int RIGHT_MARGIN_COMPASS = 0;

	// Height and Width for the compass image
	private static int HEIGHT = 140;
	private static int WIDTH = 140;

	// The circle area specified by search_radius and input lat/lon serves searching purpose.
	// It is also used to construct the extent which map zooms to after the first GPS fix is retrieved.
	private final static double SEARCH_RADIUS = 5;

	private static final String DAY_MAP="2161ba8a41114947bc7c533a24bdb150";
	private static final String NIGHT_MAP="b454f8d950054d419e053dde0c9269ba";
	private static final String ALT_MAP="9f5aa3cc27c24447bd46a11ec586c904";

    private String mBasemapPortalItemId;

    //Sound
	TTSManager ttsManager = null;
	private String mSoundActive;

	//FrameLayout for the MapView
	private FrameLayout mMapContainer;
	public static MapView mMapView;
	private String mMapViewState;

	//Variable global para guardar los limites del mapa
	Envelope maxExtent = null;

	Point center = new Point(0,0);
	Boolean ignoreTap = false;
	boolean tap = false;
	boolean dragged = false;

	static  boolean myLoc = true;
	static boolean editMode = false;
	// GPS location tracking
	private boolean mIsLocationTracking;
	private Point mLocation = null;
	private LocationManager locManager = null;
	private AlertDialog alert = null;
	private String gpsActive="apagado";

	public static boolean showBar=true;
	public void setEditButton(MenuItem editButton) {
		this.editButton = editButton;
	}

	private MenuItem editButton;
	// Graphics layer to show geocode and reverse geocode results
	private GraphicsLayer mLocationLayer;
	private Point mLocationLayerPoint;
	private String mLocationLayerPointString;

	// Graphics layer to show routes
	private GraphicsLayer mRouteLayer;
	private List<RouteDirection> mRoutingDirections;

	// Spatial references used for projecting points
	private final SpatialReference mWm = SpatialReference.create(102100);
	private final SpatialReference mEgs = SpatialReference.create(4326);

    //Compass
	Compass mCompass;
	LayoutParams compassFrameParams;
	private MotionEvent mLongPressEvent;

	private View mEditMenu;
	private HashMap<String,Point> editPointList = new HashMap<String,Point>();
	private int editPoints = 0;
	private List<Integer> editMarkers = new LinkedList<Integer>();
	private List<Integer> editMarkerNames = new LinkedList<Integer>();

	@SuppressWarnings("rawtypes")
	// - using this only to cancel pending tasks in a generic way
	private AsyncTask mPendingTask;
	private View mSearchBox;
	private View mSearchResult;
	private LayoutInflater mInflater;
	private String mStartLocation, mEndLocation;
	private  List<String> sitios;
	private static boolean guiadoEstado = false;
	private static boolean banderaFin=false;
	private static boolean banderaEdicion = false;

	private AutoCompleteTextView autoCompleteTextView;

	public LocationManager locationManager;

	public static MapFragment newSoundInstance(String basemapPortalItemId, String changeSound) {
        MapFragment mapFragment = new MapFragment();

        Bundle args = new Bundle();
        args.putString(KEY_BASEMAP_ITEM, basemapPortalItemId);
        args.putString(KEY_SOUND_ITEM, changeSound);

        mapFragment.setArguments(args);
        return mapFragment;
    }

	public MapFragment() {
		// make MapFragment ctor private - use newInstance() instead
	}

	ProgressDialog progressDoalog;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

        setHasOptionsMenu(true);

        Bundle args = savedInstanceState != null ? savedInstanceState
                : getArguments();
        if (args != null) {
            mIsLocationTracking = args.getBoolean(KEY_IS_LOCATION_TRACKING);
            mBasemapPortalItemId = args.getString(KEY_BASEMAP_ITEM);
            mSoundActive = args.getString(KEY_SOUND_ITEM);
        }


		// Calling setRetainInstance() causes the Fragment instance to be retained when its Activity is destroyed and
        // recreated. This allows map Layer objects to be retained so data will not need to be fetched from the network again.
        setRetainInstance(true);

        ttsManager = new TTSManager();
        ttsManager.init(getActivity());

	}


    @Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
		//Clave para licenciamiento gratuito
		ArcGISRuntime.setClientId("eACA1B4bnlmT8rPm");


        mMapContainer = (FrameLayout) inflater.inflate(R.layout.map_fragment_layout,container,false);

		if (mBasemapPortalItemId != null) {
			// show a map with the basemap represented by mBasemapPortalItemId
			loadWebMapIntoMapView(mBasemapPortalItemId, new Portal("http://www.arcgis.com", null));
		} else {
			// show the default map
			String defaultBaseMapURL = getString(R.string.default_basemap_url);
			MapView mapView = new MapView(getActivity(), defaultBaseMapURL,"", "");
			mBasemapPortalItemId = defaultBaseMapURL.substring(defaultBaseMapURL.indexOf("id=") + 3);
			// Set the MapView
			setMapView(mapView);
			//mapView.zoomin();
		}
		return mMapContainer;
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		super.onCreateOptionsMenu(menu, inflater);
		// Inflate the menu items for use in the action bar
		inflater.inflate(R.menu.action, menu);
		menu.findItem(R.id.editMode).setVisible(showBar);
		//menu.findItem(R.id.location).setVisible(showBar);
	}

	public boolean onOptionsItemSelected(MenuItem item) {
		//if(!MainActivity.mDrawerLayout.isDrawerOpen(GravityCompat.START) || true){
			switch (item.getItemId()) {

			case R.id.location:
				// Toggle location tracking on or off
				if (mIsLocationTracking) {
					item.setIcon(R.drawable.ic_action_compass_mode);
					mMapView.getLocationDisplayManager().setAutoPanMode(AutoPanMode.COMPASS);
					mCompass.start();
					mCompass.setVisibility(View.VISIBLE);
					mIsLocationTracking = false;
				} else {
					startLocationTracking();
					item.setIcon(android.R.drawable.ic_menu_mylocation);
					if (mMapView.getRotationAngle() != 0) {
						mCompass.setVisibility(View.VISIBLE);
						mCompass.setRotationAngle(mMapView.getRotationAngle());
					} else {
						mCompass.setVisibility(View.GONE);
					}

				}
				return true;
			case R.id.editMode:
				if(!editMode){
					banderaEdicion=true;
					this.editMode = true;
					this.showBar = false;
					this.editButton = item;
					item.setVisible(false);
					this.showEditionMenu();
					MainActivity.mDrawerLayout.closeDrawers();
					//item.setIcon(null);
					//item.setTitle("");
				}
				return true;
				case R.id.myLoc:
					if(myLoc){
						this.retornaU();
						MainActivity.mDrawerLayout.closeDrawers();

					}
				return true;

			default:
				return super.onOptionsItemSelected(item);
			}
		//}else{
		//	return super.onOptionsItemSelected(item);
		//}
	}

	@Override
	public void onPause() {
		super.onPause();

		// Pause the MapView and stop the LocationDisplayManager to save battery
		if (mMapView != null) {
			if (mIsLocationTracking) {
				mMapView.getLocationDisplayManager().stop();
				mCompass.stop();
			}
			mMapViewState = mMapView.retainState();
			mMapView.pause();
		}
	}

	@Override
	public void onResume() {
		super.onResume();

		// Start the MapView and LocationDisplayManager running again
        if (mMapView != null) {
            mMapView.unpause();
			if (mMapViewState != null) {
				mMapView.restoreState(mMapViewState);
			}
			if (mIsLocationTracking) {
				mMapView.getLocationDisplayManager().start();
			}
		}
	}

	@Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(KEY_SOUND_ITEM, mSoundActive);
		outState.putString(KEY_BASEMAP_ITEM, mBasemapPortalItemId);
		outState.putBoolean(KEY_IS_LOCATION_TRACKING, mIsLocationTracking);

	}

    /**
     * Loads a WebMap and creates a MapView from it which is set into the
     * fragment's layout.
     * @param basemapPortalItemId
     *            The portal item id that represents the basemap.
     * @throws Exception
     *             if WebMap loading failed.
     */
    private void loadWebMapIntoMapView(final String basemapPortalItemId, final Portal portal) {

        TaskExecutor.getInstance().getThreadPool().submit(new Callable<Void>() {

			@Override
			public Void call() throws Exception {

				// load a WebMap instance from the portal item
				final WebMap webmap = WebMap.newInstance(basemapPortalItemId, portal);

				// load the WebMap that represents the basemap if one was specified
				WebMap basemapWebMap = null;
				if (basemapPortalItemId != null && !basemapPortalItemId.isEmpty()) {
					basemapWebMap = WebMap.newInstance(basemapPortalItemId, portal);
				}
				final BaseMap basemap = basemapWebMap != null ? basemapWebMap.getBaseMap() : null;

				if (webmap != null) {
					getActivity().runOnUiThread(new Runnable() {
						@Override
						public void run() {
							MapView mapView = new MapView(getActivity(), webmap, basemap, null, null);

							setMapView(mapView);
							mapView.zoomin();

						}
					});
				} else {
					throw new Exception("Ocurrió un error al cargar el mapa");
				}
				return null;
			}
		});
    }


	/**
	 * Takes a MapView that has already been instantiated to show a WebMap, completes its setup by setting
     * various listeners and attributes, and sets it as the activity's content view.
	 * @param mapView
	 */
	private void setMapView(final MapView mapView) {

		mMapView = mapView;
		mMapView.setEsriLogoVisible(false);
		mMapView.enableWrapAround(true);

		mapView.setAllowRotationByPinch(true);

		//Coloco el maximo de zoom out que me permitira
		mMapView.setMinScale(10500.00);
        mMapView.setMaxScale(500.00);


		//Initializing sound
        initializeSound();

		// Creating an inflater
		mInflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);

		// Setting up the layout params for the searchview and searchresult layout
		mlayoutParams = new FrameLayout.LayoutParams(LayoutParams.MATCH_PARENT,LayoutParams.WRAP_CONTENT, Gravity.LEFT | Gravity.TOP);
		mlayoutParams.setMargins(LEFT_MARGIN_SEARCH, TOP_MARGIN_SEARCH, RIGHT_MARGIN_SEARCH, BOTTOM_MARGIN_SEARCH);

		// set MapView into the activity layout
		mMapContainer.addView(mMapView);

		// Displaying the searchbox layout
		showSearchBoxLayout();


		mMapView.setOnPinchListener(new OnPinchListener() {
			/**
			 * Default value
			 */
			private static final long serialVersionUID = 1L;

			@Override
			public void postPointersDown(float x1, float y1, float x2, float y2, double factor) {
			}

			@Override
			public void postPointersMove(float x1, float y1, float x2, float y2, double factor) {
			}

			@Override
			public void postPointersUp(float x1, float y1, float x2, float y2, double factor) {
			}

			@Override
			public void prePointersDown(float x1, float y1, float x2, float y2, double factor) {
			}

			@Override
			public void prePointersMove(float x1, float y1, float x2, float y2, double factor) {
				if (mMapView.getRotationAngle() > 5 || mMapView.getRotationAngle() < -5) {
					mCompass.setVisibility(View.VISIBLE);
					mCompass.sensorManager.unregisterListener(mCompass.sensorEventListener);
					mCompass.setRotationAngle(mMapView.getRotationAngle());
				}
			}

			@Override
			public void prePointersUp(float x1, float y1, float x2, float y2, double factor) {
			}

		});

		// Setup listener for map initialized
		mMapView.setOnStatusChangedListener(new OnStatusChangedListener() {

			private static final long serialVersionUID = 1L;

			@Override
			public void onStatusChanged(Object source, STATUS status) {

				if (source == mMapView && status == STATUS.INITIALIZED) {
					if (mMapViewState == null) {
						// Starting location tracking will cause zoom to My Location
						startLocationTracking();
					} else {
						mMapView.restoreState(mMapViewState);
					}
					// add search and routing layers
					addGraphicLayers();

				}
			}

		});


		// Setup use of magnifier on a long press on the map
		mMapView.setShowMagnifierOnLongPress(true);
		mLongPressEvent = null;


		// Setup OnTouchListener to detect and act on long-press
		mMapView.setOnTouchListener(new MapOnTouchListener(getActivity(),
				mMapView) {

			private static final int MAX_CLICK_DURATION = 100;
			private long startClickTime;

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				if (event.getActionMasked() == MotionEvent.ACTION_DOWN) {
					// Start of a new gesture. Make sure mLongPressEvent is cleared.
					mLongPressEvent = null;
					tap = false;
					center = mapView.getCenter();
					ignoreTap = false;
					dragged = false;
					startClickTime = Calendar.getInstance().getTimeInMillis();
				}
				if (event.getActionMasked() == MotionEvent.ACTION_UP) {
					tap = true;
					long clickDuration = Calendar.getInstance().getTimeInMillis() - startClickTime;
					if (clickDuration > MAX_CLICK_DURATION) {
						dragged = true;
					}
				}
				if (event.getActionMasked() == MotionEvent.ACTION_POINTER_UP) {
					if (center != mapView.getCenter())
						ignoreTap = true;
				}
				/*
				if (tap) {
					System.out.println("Tap esta activo");
				} else {
					System.out.println("Tap no esta activo");
				}

				if (dragged) {
					System.out.println("Dragged esta activo");
				} else {
					System.out.println("Dragged no esta activo");
				}*/
				if (mLongPressEvent == null && !ignoreTap && event.getPointerCount() == 1 && tap && !dragged && editMode) {
					Point mapPoint = mMapView.toMapPoint(event.getX(), event.getY());
					int icono, letra;
					if (DAY_MAP.equals(mBasemapPortalItemId)) {
						icono = R.drawable.pin_circle_purple;
						letra = Color.BLACK;
					} else if (NIGHT_MAP.equals(mBasemapPortalItemId)) {
						icono = R.drawable.pin_circle_yellow;
						//letra = Color.WHITE;
						letra=Color.rgb(255,140,0);
					} else if (ALT_MAP.equals(mBasemapPortalItemId)) {
						icono = R.drawable.pin_circle_green;
						letra = Color.BLACK;
					} else {
						icono = R.drawable.pin_circle_purple;
						letra = Color.BLACK;
					}
					Drawable drawable = getActivity().getResources().getDrawable(icono);
					PictureMarkerSymbol resultSymbol = new PictureMarkerSymbol(getActivity(), drawable);
					// create graphic object for resulting location
					Graphic resultLocGraphic = new Graphic(mapPoint, resultSymbol);
					// add graphic to location layer
					editMarkers.add(mLocationLayer.addGraphic(resultLocGraphic));
					editPoints++;

					TextSymbol text = new TextSymbol(FontStyle.ITALIC.name(), Integer.toString(editPoints), letra);
					text.setHorizontalAlignment(TextSymbol.HorizontalAlignment.CENTER);
					if(NIGHT_MAP.equals(mBasemapPortalItemId)){
						text.setFontDecoration(FontDecoration.UNDERLINE);
						//text.setFontFamily("serif");

					}
					//text.setFontDecoration(FontDecoration.LINE_THROUGH);
					//text.setColor(255);
					text.setFontWeight(FontWeight.BOLD);
					text.setSize(25);
					text.setOffsetY(15);
					editMarkerNames.add(mLocationLayer.addGraphic(new Graphic(mapPoint, text)));
					editPointList.put(Integer.toString(editPoints), mapPoint);
					tap = false;
				}
				return super.onTouch(v, event);
			}


			@Override
			public void onLongPress(MotionEvent point) {
				// Set mLongPressEvent to indicate we are processing a long-press
				mLongPressEvent = point;
				super.onLongPress(point);

			}


			@Override
			public boolean onDragPointerUp(MotionEvent from, final MotionEvent to) {
				if (mLongPressEvent != null && !editMode) {
					// This is the end of a long-press that will have displayed the magnifier.
					// Perform reverse-geocoding of the point that was pressed
					Point mapPoint = mMapView.toMapPoint(to.getX(), to.getY());
					ReverseGeocodingAsyncTask reverseGeocodeTask = new ReverseGeocodingAsyncTask();
					reverseGeocodeTask.execute(mapPoint);
					mPendingTask = reverseGeocodeTask;
					mLongPressEvent = null;
					TheresAPlace = true;
					// Remove any previous graphics
					resetGraphicsLayers();

				} else {
					TheresAPlace = false;
				}
				return super.onDragPointerUp(from, to);
			}

		});

	}


	/**
	 * Adds the compass as per the height of the layout
	 * @param height
	 */
	private void addCompass(int height) {

		mMapContainer.removeView(mCompass);

		// Create the Compass custom view, and add it onto the MapView.
		mCompass = new Compass(mMapView.getContext());
		mCompass.setAlpha(1f);
		mCompass.setRotationAngle(45);
		compassFrameParams = new FrameLayout.LayoutParams(HEIGHT, WIDTH,Gravity.RIGHT);

		TOP_MARGIN_COMPASS = TOP_MARGIN_SEARCH + height + 15;

		((MarginLayoutParams) compassFrameParams).setMargins(LEFT_MARGIN_COMPASS, TOP_MARGIN_COMPASS, RIGHT_MARGIN_COMPASS, BOTTOM_MARGIN_COMPASS);

		mCompass.setLayoutParams(compassFrameParams);

		mCompass.setVisibility(View.GONE);


		mCompass.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				mCompass.setVisibility(View.GONE);
				mMapView.setRotationAngle(0);
			}
		});

		// Add the compass on the map
		mMapContainer.addView(mCompass);
	}

    /**
     * Initialize the sound depending on the variable in the MainActivity
     */
    private void initializeSound(){

        MainActivity main = (MainActivity) getActivity();
        mSoundActive = main.getChangeSound();

        if (mSoundActive.equals("Sonido Encendido")) {
            Toast.makeText(getActivity(),mSoundActive, Toast.LENGTH_LONG).show();
        }
        else{
            ttsManager.shutDown();
            Toast.makeText(getActivity(),mSoundActive, Toast.LENGTH_LONG).show();
        }

    }

    /**
	 * Displays the Dialog Fragment which allows users to route
	 */
	private void showRoutingDialogFragment() {
		// Show RoutingDialogFragment to get routing start and end points.
		// This calls back to onGetRoute() to do the routing.
		RoutingDialogFragment routingFrag = new RoutingDialogFragment();
		routingFrag.setRoutingDialogListener(this);
		Bundle arguments = new Bundle();
		if (mLocationLayerPoint != null) {
			arguments.putString(RoutingDialogFragment.ARG_END_POINT_DEFAULT, mLocationLayerPointString);
		}

		routingFrag.setArguments(arguments);
		routingFrag.show(getFragmentManager(), null);

	}

	public void mostrarSecuenciaInstruccion(RouteDirection direction, String text, int i){
		Toast.makeText(getActivity(), text, Toast.LENGTH_LONG).show();
		if(i==0)
			ttsManager.initQueue(text);
		else
			ttsManager.addQueue(text);
		mMapView.setExtent(direction.getGeometry());
		//  showDirectionsDialogFragment();

		//if(banderaFin) return;

		try {
			Thread.sleep(5000);

		} catch (InterruptedException e) {
			e.printStackTrace();
		}

	}

	public void modoGuiado(){

		Integer tamanio=mRoutingDirections.size();
		RouteDirection direction;
		String text;
		if (mSoundActive.equalsIgnoreCase("Sonido Encendido")) {
			int i=0;

			while(i< tamanio){
				//if(banderaFin) break;
				direction = mRoutingDirections.get(i);
				text = mRoutingDirections.get(i).getText(); //getting the direction

				mostrarSecuenciaInstruccion(direction,text, i);
				i++;


			}




		}


	}



	public void alertaModoPasos(){
			showDirectionsDialogFragment();

	}


	public void alertaModoGuiadoPasos(){
		if(!guiadoEstado) {
			final AlertDialog.Builder builderGuiado = new AlertDialog.Builder(getActivity());
			builderGuiado.setMessage("¿Cómo te gustaría recibir las instrucciones?")
					.setCancelable(false)
					.setTitle("Ruta a Seguir")
					.setIcon(R.drawable.nogps)
					.setPositiveButton("Pasos y Más", new DialogInterface.OnClickListener() {
						@Override
						public void onClick(@SuppressWarnings("unused") final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
							guiadoEstado=true;
							showDirectionsDialogFragment();
						}
					})
					.setNegativeButton("Modo Guiado", new DialogInterface.OnClickListener() {
						@Override
						public void onClick(final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
							guiadoEstado=false;
							dialog.cancel();
							modoGuiado();
						}
					});
			alert = builderGuiado.create();
			alert.show();
		}else{
			showDirectionsDialogFragment();
		}
	}


	/**
	 * Displays the Directions Dialog Fragment
	 */
	private void showDirectionsDialogFragment() {
		// Launch a DirectionsListFragment to display list of directions

		final DirectionsDialogFragment frag = new DirectionsDialogFragment();


		frag.setRoutingDirections(mRoutingDirections,
				new DirectionsDialogListener() {

					@Override
					public void onDirectionSelected(int position) {
						// User has selected a particular direction dismiss the dialog and zoom to the selected direction
						frag.dismiss();
						//Toast.makeText(getActivity(),String.valueOf(mRoutingDirections.size()),Toast.LENGTH_SHORT).show();
						Integer tamanio=mRoutingDirections.size();
						RouteDirection direction=mRoutingDirections.get(position);
						String txt=mRoutingDirections.get(position).getText(); //getting the direction
						mMapView.setExtent(direction.getGeometry());
						//Reads the direction with sound
						if (mSoundActive.equalsIgnoreCase("Sonido Encendido")) {
						    //  Toast.makeText(getActivity(), txt, Toast.LENGTH_LONG).show();
							//Toast.makeText(getActivity(),String.valueOf(position) +":"+String.valueOf(tamanio),Toast.LENGTH_SHORT).show();
						      if(position == (tamanio - 1)){
						      	guiadoEstado=false;
							  }
                              ttsManager.initQueue(txt);

							}

						}


				});
		getFragmentManager().beginTransaction().add(frag, null).commit();
	}

    /**
     * Releases the resources used by the TextToSpeech engine.
     */
    @Override
    public void onDestroy() {
        super.onDestroy();
        ttsManager.shutDown();
    }

	/**
	 * Displays the search view layout
	 */
	private void showSearchBoxLayout() {

		// Inflating the layout from the xml file
		mSearchBox = mInflater.inflate(R.layout.searchview, null);

		// Setting the layout parameters to the layout
		mSearchBox.setLayoutParams(mlayoutParams);

		//Creando arreglo de datos (SITIOS) para opciones del autoComplete

		// TENGO QUE HACER LLAMADA AL WEB SERVICE PARA CARGAR TODOS LOS SITIOS
		//AGREGARLOS AL ARREGLO sitios.
		sitios= new ArrayList<>();
		List<String>sitios2 = new ArrayList<>();

		new SitioAsyncTask().execute(sitios);

		autoCompleteTextView = (AutoCompleteTextView) mSearchBox.findViewById(R.id.searchView1);

		//validar que la lista de sitios que se obtuvo de la base no esté vacía




		ImageView iv_route = (ImageView) mSearchBox.findViewById(R.id.imageView1);
		ImageView lupa = (ImageView) mSearchBox.findViewById(R.id.lupasearch);

		// Adding the layout to the map conatiner
		mMapContainer.addView(mSearchBox);

		// Setup the listener for the route onclick
		iv_route.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				banderaEdicion=false;
				showRoutingDialogFragment();
			}
		});

		//evento en boton lupa para el text autoComplete
		lupa.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				onSearchButtonClicked(autoCompleteTextView.getText().toString());
				autoCompleteTextView.clearFocus();
			}
		});

		//Evento "Done" desde teclado
		autoCompleteTextView.setOnKeyListener(new View.OnKeyListener() {
			@Override
			public boolean onKey(View v, int keyCode, KeyEvent event) {
				if(event.getAction()== KeyEvent.ACTION_DOWN && keyCode==KeyEvent.KEYCODE_ENTER){
					onSearchButtonClicked(autoCompleteTextView.getText().toString());
					autoCompleteTextView.clearFocus();
					return true;
				}

				return false;
			}
		});

		// Add the compass after getting the height of the layout
		mSearchBox.getViewTreeObserver().addOnGlobalLayoutListener(
				new OnGlobalLayoutListener() {
					@Override
					public void onGlobalLayout() {
						addCompass(mSearchBox.getHeight());
						mSearchBox.getViewTreeObserver().removeGlobalOnLayoutListener(this);
					}

				});

	}

	/**
	 * Clears all graphics out of the location layer and the route layer.
	 */
	void resetGraphicsLayers() {
		mLocationLayer.removeAll();
		mRouteLayer.removeAll();
		mLocationLayerPoint = null;
		mLocationLayerPointString = null;
		mRoutingDirections = null;
	}

	/**
	 * Adds location layer and the route layer to the MapView.
	 */
	void addGraphicLayers() {
		// Add location layer
		if (mLocationLayer == null) {
			mLocationLayer = new GraphicsLayer();
		}
		mMapView.addLayer(mLocationLayer);

		// Add the route graphic layer
		if (mRouteLayer == null) {
			mRouteLayer = new GraphicsLayer();
		}
		mMapView.addLayer(mRouteLayer);
	}

	/**
	 * Starts tracking GPS location.
	 */
	void startLocationTracking() {
		LocationDisplayManager locDispMgr = mMapView.getLocationDisplayManager();
		mCompass.start();
		locDispMgr.setAutoPanMode(AutoPanMode.LOCATION);
		locDispMgr.setAllowNetworkLocation(true);


		locDispMgr.setLocationListener(new LocationListener() {

			boolean locationChanged = false;

			// Zooms to the current location when first GPS fix arrives
			@Override
			public void onLocationChanged(Location loc) {
				double locy = loc.getLatitude();
				double locx = loc.getLongitude();
				Point wgspoint;

				//edit
				Location lMapa = new Location("");
				/*COORDENADAS UCA lat: 13.680582 lon: -89.236678 */
				lMapa.setLatitude(13.681000);
				lMapa.setLongitude(-89.235442);
				float distanceInMeters = loc.distanceTo(lMapa);
				if (distanceInMeters <= 300) {
					wgspoint = new Point(locx, locy);

				} else {
					wgspoint = new Point(lMapa.getLatitude(),lMapa.getLongitude());
					//Toast.makeText(getActivity(), "Te encuentras fuera de los limites de la UCA, pero puedes hacer uso de la app.", Toast.LENGTH_SHORT).show();
				}
				//Toast.makeText(getActivity(),String.valueOf(distanceInMeters)+" mts", Toast.LENGTH_SHORT).show();

				mLocation = (Point) GeometryEngine.project(wgspoint, SpatialReference.create(4326), mMapView.getSpatialReference());


				if (!locationChanged) {
					locationChanged = true;
					Unit mapUnit = mMapView.getSpatialReference().getUnit();
					double zoomWidth = Unit.convertUnits(SEARCH_RADIUS, Unit.create(LinearUnit.Code.METER), mapUnit);
					Envelope zoomExtent = new Envelope(mLocation, zoomWidth/10, zoomWidth/10);
					mMapView.setExtent(zoomExtent);
				}

			}

			@Override
			public void onProviderDisabled(String arg0) {
				if(!gpsActive.equals("THINKING")){
					gpsActive="OFF";
				}
				Toast.makeText(getActivity(), "GPS apagado", Toast.LENGTH_SHORT).show();
				if(gpsActive.equals("OFF")) {
					if(contadorGpsAlert < 3) {
						contadorGpsAlert = contadorGpsAlert + 1;
						alertNoGps(getActivity());
					}
				}
			}

			@Override
			public void onProviderEnabled(String arg0) {
				if(gpsActive.equals("THINKING")){
					gpsActive="ON";
				}
				Toast.makeText(getActivity(), "GPS encendido", Toast.LENGTH_SHORT).show();
			}

			@Override
			public void onStatusChanged(String arg0, int arg1, Bundle arg2) {
				if(gpsActive.equals("THINKING")){
					gpsActive="ON";
				}else{
					gpsActive="OFF";
				}
			}
		});

		locDispMgr.start();
		mIsLocationTracking = true;
	}

	//Funcion para regresar a la vista inicial

	public void retornaU() {

			mMapView.centerAt(13.681000, -89.235442, true);
			Toast.makeText(getActivity(), "Fuiste enviado a la UCA, nuevamente.", Toast.LENGTH_SHORT).show();

    }

	// ALERTA PARA INDICAR AL USUARIO QUE PUEDE ACTIVAR GPS PARA OBTNER LA UBICACION
	// EN CASO NO PUEDA ACCEDER A LA RED DE LA UNIVERSIDAD

	public void alertNoGps(final Activity activity) {
		gpsActive="THINKING";
		final AlertDialog.Builder builder = new AlertDialog.Builder(activity);
		builder.setMessage("Sistema GPS desactivado. \n¿Quieres activarlo?")
				.setCancelable(false)
				.setTitle("Alerta GPS")
				.setIcon(R.drawable.nogps)
				.setPositiveButton("Si", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(@SuppressWarnings("unused") final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
						gpsActive="ON";
						startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
					}
				})
				.setNegativeButton("No", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
						dialog.cancel();
						gpsActive="OFF";
					}
				});
		alert = builder.create();
		alert.show();
	}
	/**
	 * Called from search_layout.xml when user presses Search button.
     */
	public void onSearchButtonClicked(String address) {

		// Hide virtual keyboard
		InputMethodManager inputManager = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
		inputManager.hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(), 0);

		// Remove any previous graphics and routes
		resetGraphicsLayers();
		LocatorAsyncPreTask lapt = new LocatorAsyncPreTask();
		lapt.execute(address);
		//executeLocatorTask(address);
	}

	public void onAdvanceSearchLocate(String address){
		resetGraphicsLayers();
		executeLocatorTask(address);
	}
	/**
	 * Set up the search parameters and execute the Locator task.
	 * @param address
	 */
	public void executeLocatorTask(String address) {
		// Create Locator parameters from single line address string
		LocatorFindParameters findParams = new LocatorFindParameters(address);

		// Use the centre of the current map extent as the find location point
		findParams.setLocation(mMapView.getCenter(),mMapView.getSpatialReference());

		// Calculate distance for find operation
		Envelope mapExtent = new Envelope();
		mMapView.getExtent().queryEnvelope(mapExtent);
		// assume map is in metres, other units wont work, double current envelope
		double distance = (mapExtent != null && mapExtent.getWidth() > 0) ? mapExtent.getWidth() * 2 : 10000;
		findParams.setDistance(distance);
		findParams.setMaxLocations(2);

		// Set address spatial reference to match map
		findParams.setOutSR(mMapView.getSpatialReference());

		// Execute async task to find the address
		LocatorAsyncTask locatorTask = new LocatorAsyncTask();
		locatorTask.execute(findParams);
		mPendingTask = locatorTask;

		mLocationLayerPointString = address;
	}

	/**
	 * Called by RoutingDialogFragment when user presses Get Route button.
	 * @param startPoint
	 *            String entered by user to define start point.
	 * @param endPoint
	 *            String entered by user to define end point.
	 * @return true if routing task executed, false if parameters rejected. If
	 *         this method rejects the parameters it must display an explanatory
	 *         Toast to the user before returning.
	 */
	@Override
	public boolean onGetRoute(String startPoint, String endPoint) {
		// Check if we need a location fix
		if (startPoint.equals(getString(R.string.my_location)) && mLocation == null) {
			Toast.makeText(getActivity(),getString(R.string.need_location_fix), Toast.LENGTH_LONG).show();
			return false;
		}
		// Remove any previous graphics and routes
		resetGraphicsLayers();
		// Do the routing
		executeRoutingTask(startPoint, endPoint);
		return true;
	}

	@Override
	public boolean onGetRouteFavorite(String startname,String endName,double startLatitud, double startLongitud, double endLatitud, double endLongitud) {
		// Remove any previous graphics and routes
		resetGraphicsLayers();
		// Do the routing
		executeRoutingTask(startname, endName, startLatitud, startLongitud, endLatitud, endLongitud);
		return true;
	}

	@Override
	public boolean onGetRouteMultiple(RutaEspecial ruta,int tipo){
		// Remove any previous graphics and routes
		resetGraphicsLayers();
		// Do the routing
		executeMultipleRoutingTask(ruta, tipo);
		return true;
	}


	public void onGetRouteMarked(String startPoint, String endPoint){
		resetGraphicsLayers();
		// Do the routing
		executeRoutingTask(startPoint, endPoint);
	}

	/**
	 * Set up Route Parameters to execute RouteTask
	 * @param start
	 * @param end
	 */
	@SuppressWarnings("unchecked")
	private void executeRoutingTask(String start, String end) {

        // Create a list of start end point params
		LocatorFindParameters routeStartParams = new LocatorFindParameters(start);
		LocatorFindParameters routeEndParams = new LocatorFindParameters(end);
		List<LocatorFindParameters> routeParams = new ArrayList<LocatorFindParameters>();

		// Add params to list
		routeParams.add(routeStartParams);
		routeParams.add(routeEndParams);

        // Execute async task to do the routing
		RouteAsyncTask routeTask = new RouteAsyncTask();
		routeTask.setTipo(0);
		routeTask.execute(routeParams);
		mPendingTask = routeTask;
	}

	private void executeRoutingTask(String start, String end,double startLatitud, double startLongitud, double endLatitud, double endLongitud) {

		// Create a list of start end point params
		LocatorFindParameters routeStartParams = new LocatorFindParameters(start);
		LocatorFindParameters routeEndParams = new LocatorFindParameters(end);
		List<LocatorFindParameters> routeParams = new ArrayList<LocatorFindParameters>();
		Point puntoIncio = new Point();
		puntoIncio.setXY(startLongitud,startLatitud);

		Point puntoFin = new Point();
		puntoFin.setXY(endLongitud,endLatitud);

		routeStartParams.setLocation(puntoIncio, mWm);
		routeEndParams.setLocation(puntoFin,mWm);
		// Add params to list
		routeParams.add(routeStartParams);
		routeParams.add(routeEndParams);

		// Execute async task to do the routing
		RouteAsyncTask routeTask = new RouteAsyncTask();
		routeTask.setTipo(0);
		routeTask.execute(routeParams);
		mPendingTask = routeTask;
	}

	private void executeMultipleRoutingTask(final RutaEspecial ruta,int tipo){
		volley = volleySingleton.getInstance(getActivity().getApplicationContext());
		requestQueue = volley.getRequestQueue();
		List<LocatorFindParameters> routeParams = new ArrayList<LocatorFindParameters>();
		List<String> nomEdi = new LinkedList<String>();
		//agregamos el nombre de la ruta
		nomEdi.add(ruta.getNombre());
		String [] puntosString = ruta.getPuntos().split("/");

		for(String cadena: puntosString){
			String[] puntoInformacion = cadena.split(",");
			if(puntoInformacion[0].equals("NONE")){
			}else {//añadimos los nombres de los edificios mientras no sean NONE
				nomEdi.add(puntoInformacion[0]);
			}
			LocatorFindParameters param = new LocatorFindParameters(puntoInformacion[0]);
			Point punto = new Point();
			double x = Double.parseDouble(puntoInformacion[1]);
			double y = Double.parseDouble(puntoInformacion[2]);
			punto.setXY(x, y);
			param.setLocation(punto, mWm);
			routeParams.add(param);
		}

		RouteAsyncTask routeTask = new RouteAsyncTask();
		routeTask.setTipo(tipo);
		routeTask.setNombreEdificios(nomEdi);
		routeTask.setRuta(ruta);
		routeTask.execute(routeParams);
		mPendingTask = routeTask;

	}

	public void addtoQueue(Request request) {
		if (request != null) {
			request.setTag(this);
			if (requestQueue == null)
				requestQueue = volley.getRequestQueue();
			request.setRetryPolicy(new DefaultRetryPolicy(
					600000, 3, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
			));
			requestQueue.add(request);
		}
	}

	public void obtenerImagen(String ruta, final DialogInfoRoutes dialogo){

		String urlImagen = Constantes.BASE + ruta;
		ImageRequest request = new ImageRequest(
				urlImagen,
				new Response.Listener<Bitmap>() {
					@Override
					public void onResponse(Bitmap bitmap) {
						dialogo.setImagen(bitmap);
						dialogo.show(getFragmentManager(),"Dialog Route");
					}
				}, 0, 0, null,
				new Response.ErrorListener() {
					public void onErrorResponse(VolleyError error) {
						Log.d("En el get imagen ruta",error.toString());
						Bitmap bitFalso = BitmapFactory.decodeResource(getActivity().getResources(),R.drawable.logouca);
						dialogo.setImagen(bitFalso);
						dialogo.show(getFragmentManager(),"Dialog Route");
					}
				});
		addtoQueue(request);
	}


	@Override
	public void onCancel(DialogInterface dialog) {
		// a pending task needs to be canceled
		if (mPendingTask != null) {
			mPendingTask.cancel(true);
		}
	}

	public void showEditionMenu(){
		resetGraphicsLayers();
		mMapContainer.removeView(mSearchBox);
		mMapContainer.removeView(mSearchResult);

		mEditMenu = mInflater.inflate(R.layout.multi_point_actions,null);
		mEditMenu.setLayoutParams(mlayoutParams);
		ImageView iv_cancel = (ImageView) mEditMenu.findViewById(R.id.imageQuit);

		iv_cancel.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// Remove the search result view
				mMapContainer.removeView(mEditMenu);	//REMUEVE BARRA DE EDICION
				// Add the search box view
				showSearchBoxLayout();
				// Remove all graphics from the map
				editMarkers.clear();
				editMarkerNames.clear();
				editPointList.clear();
				editPoints=0;
				editMode=false;
				showBar = true;
				if(editButton != null){editButton.setVisible(true);}
				resetGraphicsLayers();
				getActivity().invalidateOptionsMenu();
			}
		});

		ImageView iv_return = (ImageView) mEditMenu.findViewById(R.id.imageReturn);

		iv_return.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// Remove the search result view
				if(editPoints >= 1){
					mLocationLayer.removeGraphic(editMarkers.get(editMarkers.size() - 1));
					mLocationLayer.removeGraphic(editMarkerNames.get(editMarkerNames.size() - 1));
					editPointList.remove(Integer.toString(editPoints));
					editMarkers.remove(editMarkers.size() - 1);
					editMarkerNames.remove(editMarkerNames.size() - 1);
					editPoints--;
				}else{
					Toast.makeText(getActivity(),"No hay ningún punto colocado",Toast.LENGTH_SHORT).show();
				}

				//resetGraphicsLayers();

			}
		});

		ImageView iv_route = (ImageView) mEditMenu.findViewById(R.id.imageSave);

		iv_route.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if(editPoints >= 2) {
					guiadoEstado=true;
					// Remove the search result view
					editMarkers.clear();
					editMarkerNames.clear();
					editMode = false;
					showBar = true;
					resetGraphicsLayers();
					getActivity().invalidateOptionsMenu();
					RutaEspecial rutaMultiple = new RutaEspecial();
					rutaMultiple.setDescripcion("ruta multiple");
					rutaMultiple.setIdRutaEspecial("42");
					rutaMultiple.setNombre("Ruta Multiple Generada");
					rutaMultiple.setPuntos("");
					boolean ultima = false;
					for (int i = 0; i < editPointList.size(); i++) {
						if (i + 1 == editPointList.size()) {
							ultima = true;
						}
						Point punto = editPointList.get(Integer.toString(i + 1));
						rutaMultiple.setPuntos(rutaMultiple.getPuntos() + "Punto " + (i + 1) + "," + punto.getX() + "," + punto.getY());
						if (!ultima) {
							rutaMultiple.setPuntos(rutaMultiple.getPuntos() + "/");
						}
					}

					editPointList.clear();
					editPoints = 0;

					onGetRouteMultiple(rutaMultiple,0);
				}else{
					Toast.makeText(getActivity(),"Se necesitan al menos dos puntos para trazar la ruta.",Toast.LENGTH_SHORT).show();
				}
			}
		});

		mMapContainer.addView(mEditMenu);
	}

	/**
	 * Shows the search result in the layout after successful geocoding and reverse geocoding
	 */
	private void showSearchResultLayout(final String address) {
		// Remove the layouts
		mMapContainer.removeView(mSearchBox);
		mMapContainer.removeView(mSearchResult);

		// Inflate the new layout from the xml file
		mSearchResult = mInflater.inflate(R.layout.search_result, null);

		// Set layout parameters
		mSearchResult.setLayoutParams(mlayoutParams);

		// Initialize the textview and set its text
		TextView tv = (TextView) mSearchResult.findViewById(R.id.textView1);
		tv.setTypeface(null, Typeface.BOLD);
		tv.setText(address);
        ttsManager.initQueue(address);

		// Adding the search result layout to the map container
		mMapContainer.addView(mSearchResult);

		// Setup the listener for the "cancel" icon
		ImageView iv_cancel = (ImageView) mSearchResult .findViewById(R.id.imageView3);
		iv_cancel.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// Remove the search result view
				mMapContainer.removeView(mSearchResult);
				// Add the search box view
				showSearchBoxLayout();
				// Remove all graphics from the map
				resetGraphicsLayers();

			}
		});

		// Set up the listener for the "Get Directions" icon
		ImageView iv_route = (ImageView) mSearchResult.findViewById(R.id.imageView2);
		iv_route.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				onGetRoute(getString(R.string.my_location), mLocationLayerPointString);
			}
		});

		ImageView iv_note = (ImageView) mSearchResult.findViewById(R.id.noteButton);
		iv_note.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				DialogSaveNote dsn = new DialogSaveNote();
				dsn.setEdificio(address);
				dsn.show(getFragmentManager(), "guardarNota");
			}
		});

        ImageView iv_info = (ImageView) mSearchResult.findViewById(R.id.info_place_button);
        iv_info.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                //if(TheresAPlace) {
                    //DialogFragment newFragment = new DialogInfoPlaces();
                    //newFragment.show(getFragmentManager(), "Información");
                TextView barra_busqueda = (TextView) getActivity().findViewById(R.id.textView1);
				ProgressDialog progress = new ProgressDialog(getActivity());
				CargaDetalles cd = new CargaDetalles();
                cd.fm = getFragmentManager();
				cd.setNombreEdificio(barra_busqueda.getText().toString());
				Log.d("Esto tiene la barra",barra_busqueda.getText().toString());
                Toast.makeText(getActivity(), "Cargando Informacion...",Toast.LENGTH_SHORT).show();
                cd.execute(getActivity());

            }
        });

		// Add the compass after getting the height of the layout
		mSearchResult.getViewTreeObserver().addOnGlobalLayoutListener(
				new OnGlobalLayoutListener() {
					@Override
					public void onGlobalLayout() {
						addCompass(mSearchResult.getHeight());
						mSearchResult.getViewTreeObserver().removeGlobalOnLayoutListener(this);
					}

				});

	}

	/**
	 * Shows the Routing result layout after successful routing
	 * @param distance in meters
	 */
	private void showRoutingResultLayout(double distance,int tipo,final RutaEspecial ruta, final List<String> edificios) {
        // Remove the layours
		mMapContainer.removeView(mSearchResult);
		mMapContainer.removeView(mSearchBox);

		// Inflate the new layout from the xml file
		mSearchResult = mInflater.inflate(R.layout.routing_result, null);
		mSearchResult.setLayoutParams(mlayoutParams);

		// Shorten the start and end location by finding the first comma if
		// present
		int index_from = mStartLocation.indexOf(",");
		int index_to = mEndLocation.indexOf(",");
		if (index_from != -1)
			mStartLocation = mStartLocation.substring(0, index_from);
		if (index_to != -1)
			mEndLocation = mEndLocation.substring(0, index_to);

		// Initialize the textview and display the text
			TextView tv_from = (TextView) mSearchResult.findViewById(R.id.tv_from);
			tv_from.setTypeface(null, Typeface.BOLD);
			tv_from.setText(" " + mStartLocation);

			TextView tv_to = (TextView) mSearchResult.findViewById(R.id.tv_to);
			tv_to.setTypeface(null, Typeface.BOLD);
			tv_to.setText(" " + mEndLocation);

		// Rounding off the values
		distance = Math.round((distance * 1609.344));

		TextView tv_dist = (TextView) mSearchResult.findViewById(R.id.tv_dist);
		tv_dist.setTypeface(null, Typeface.BOLD);
		tv_dist.setText(" (" + distance + " mts )");

		// Adding the layout
		mMapContainer.addView(mSearchResult);

		// Setup the listener for the "Cancel" icon
		ImageView iv_cancel = (ImageView) mSearchResult.findViewById(R.id.imageView3);
		iv_cancel.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				banderaFin=true;
				// Remove the search result view
				mMapContainer.removeView(mEditMenu);
				mMapContainer.removeView(mSearchResult);
				// Add the default search box view
				showSearchBoxLayout();
				// Remove all graphics from the map
				resetGraphicsLayers();
			}
		});


		if (tipo==1){
			ImageView iv_info = (ImageView) mSearchResult.findViewById(R.id.info_place_button);
			iv_info.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					DialogInfoRoutes diaInfoRuta = new DialogInfoRoutes();
					diaInfoRuta.setNombreRuta(ruta.getNombre());
					diaInfoRuta.setDescripcion(ruta.getDescripcion());
					diaInfoRuta.setContador(0);
					diaInfoRuta.setEdificios(edificios);
					obtenerImagen(ruta.getImagen(), diaInfoRuta);
					Toast.makeText(getActivity(), "Cargando Informacion...",Toast.LENGTH_SHORT).show();
				}
			});
			ImageView iv_save = (ImageView) mSearchResult.findViewById(R.id.imageView4);
			iv_save.setVisibility(View.GONE);
		}else {
			ImageView iv_save = (ImageView) mSearchResult.findViewById(R.id.imageView4);
			iv_save.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {

					DialogFragment newFragment = new DialogFavoriteRoute();
					newFragment.show(getFragmentManager(),"Favorites");

				}
			});
			ImageView iv_info = (ImageView) mSearchResult.findViewById(R.id.info_place_button);
			iv_info.setVisibility(View.GONE);
		}

		// Set up the listener for the "Show Directions" icon
		ImageView iv_directions = (ImageView) mSearchResult.findViewById(R.id.imageView2);
		iv_directions.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				//showDirectionsDialogFragment();
				if(banderaEdicion){
					alertaModoPasos();

				}else{

					alertaModoGuiadoPasos();
				}
			}
		});

		// Add the compass after getting the height of the layout
		mSearchResult.getViewTreeObserver().addOnGlobalLayoutListener(
				new OnGlobalLayoutListener() {
					@Override
					public void onGlobalLayout() {
						addCompass(mSearchResult.getHeight());
						mSearchResult.getViewTreeObserver().removeGlobalOnLayoutListener(this);
					}

				});

	}

	/*
	 * This class provides an AsyncTask that performs a geolocation request on a
	 * background thread and displays the first result on the map on the UI thread.
	 */
	private class LocatorAsyncPreTask extends AsyncTask<String,Void,Context>{
        private static final String TAG_INFO_SEARCH_PROGRESS_DIALOG = "TAG_INFO_SEARCH_PROGRESS_DIALOG";
        private Exception mException;
        private ProgressDialogFragment mProgressDialog;
        private volleySingleton volley;
		private RequestQueue requestQueue;
		private List<Sitio> listaSitios = new ArrayList<>();
		private String nombre;
        public LocatorAsyncPreTask() {
        }

        @Override
        protected void onPreExecute() {
            mProgressDialog = ProgressDialogFragment.newInstance("Cargando Información del lugar");
            // set the target fragment to receive cancel notification
            mProgressDialog.setTargetFragment(MapFragment.this,REQUEST_CODE_PROGRESS_DIALOG);
            mProgressDialog.show(getActivity().getFragmentManager(),TAG_INFO_SEARCH_PROGRESS_DIALOG);
        }

        @Override
		protected Context doInBackground(String... strings){
			//asignamos valores al volley y a la queue.
            mException=null;
			volley = volleySingleton.getInstance(getActivity().getApplicationContext());
			requestQueue = volley.getRequestQueue();
			//llamamos a getSitios, donde obtenemos las cosas que necesitamos
			if (strings[0].equals("Plaza Central") ||
				strings[0].equals("plaza central") ||
				strings[0].equals("plaza") ||
				strings[0].equals("Plaza")){
				nombre = "Plaza los Martires";
			}else{
			nombre = strings[0];
			}
			try {
                getSitios();

            }catch (Exception e){
                mException = e;
            }

            Context contexto = getActivity().getApplicationContext();
            return contexto;

		}

		@Override
		protected void onPostExecute(Context contexto){
			//relleno
            mProgressDialog.dismiss();
            if (mException != null) {
                Log.w(TAG, "Falló recuperar info de la base:");
                mException.printStackTrace();
                Toast.makeText(getActivity(),"Falló Recuperación de Información del Lugar",Toast.LENGTH_LONG).show();
                return;
            }
		}

		public void getSitios() {
			nombre = nombre.replaceAll("\n", "");
			nombre = URLEncoder.encode(nombre);
			String url = Constantes.GET_SITIOS + "?busqueda="+nombre+"&categoria=edificio";
			//creamos un object request, y lo añadimos a la cola
			JsonObjectRequest request = new JsonObjectRequest
					(Request.Method.GET, url,null, new Response.Listener<JSONObject>() {
						@Override
						public void onResponse(JSONObject response) {
							//cuando obtenemos una respuesta, la procesamos
							procesarRespuesta(response);
						}
					}, new Response.ErrorListener() {
						@Override
						public void onErrorResponse(VolleyError error) {
							Toast.makeText(getActivity().getApplicationContext(),"Se produjo un error: "+ error,Toast.LENGTH_LONG).show();
						}
					});
			addtoQueue(request);
		}

		private void procesarRespuesta(JSONObject response) {
			try {
				// Obtener atributo "estado"
				String estado = response.getString("estado");
				switch (estado) {
					case "1": // EXITO
						// Obtener array "sitios" Json
						JSONArray arraySitios = response.getJSONArray("sitios");
						// Parsear
						for (int i = 0; i < arraySitios.length(); i++) {
							//como se obtiene un arreglo, se guarda cada sitio en una lista
							JSONObject sitio = (JSONObject) arraySitios.get(i);
							String nombre = sitio.getString("NOMBRE");
							String nombreEdificio = sitio.getString("NOMBREEDIFICIO");
							//creamos un sitio auxiliar
							Sitio sitioAux = new Sitio();
							//lo llenamos
							sitioAux.setNombre(nombre);
							sitioAux.setNombreEdificio(nombreEdificio);
							//y lo añadimos a la lista
							listaSitios.add(sitioAux);
						}
						String[] listaResultString = new String[listaSitios.size()];
						for (int j = 0; j < listaSitios.size(); j++) {
							listaResultString[j] = listaSitios.get(j).getNombre();
						}
						if(listaSitios.size()==1){
							executeLocatorTask(listaSitios.get(0).getNombreEdificio());
						}else{
						//una vez tenemos la lista llena, hacemos un dialogito con los nombres de los lugares,
						AlertDialog.Builder sitiosResult = new AlertDialog.Builder(getActivity());
						sitiosResult.setTitle(listaSitios.size()+" resultados encontrados")
								.setItems(listaResultString, new DialogInterface.OnClickListener() {
									@Override
									public void onClick(DialogInterface dialog, int item) {
										String resultado = listaSitios.get(item).getNombreEdificio();
										executeLocatorTask(resultado);
									}
								});
						final AlertDialog alertResult = sitiosResult.create();
						alertResult.show();
						}
						break;
					case "2": // FALLIDO
						String mensaje2 = response.getString("mensaje");
						Toast.makeText(getActivity().getApplicationContext(),"Lo sentimos, "+ mensaje2,Toast.LENGTH_LONG).show();
						break;
				}

			} catch (JSONException e) {
				e.printStackTrace();
			}


		}


		public void addtoQueue(Request request) {
			if (request != null) {
				request.setTag(this);
				if (requestQueue == null)
					requestQueue = volley.getRequestQueue();
				request.setRetryPolicy(new DefaultRetryPolicy(
						600000, 3, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
				));

				requestQueue.add(request);
			}
		}
	}

	private class LocatorAsyncTask extends AsyncTask<LocatorFindParameters, Void, List<LocatorGeocodeResult>> {
		private static final String TAG_LOCATOR_PROGRESS_DIALOG = "TAG_LOCATOR_PROGRESS_DIALOG";
		private Exception mException;
		private ProgressDialogFragment mProgressDialog;
		public LocatorAsyncTask() {
		}

		@Override
		protected void onPreExecute() {
			mProgressDialog = ProgressDialogFragment.newInstance(getActivity().getString(R.string.address_search));
			// set the target fragment to receive cancel notification
			mProgressDialog.setTargetFragment(MapFragment.this,REQUEST_CODE_PROGRESS_DIALOG);
			mProgressDialog.show(getActivity().getFragmentManager(),TAG_LOCATOR_PROGRESS_DIALOG);
		}

		@Override
		protected List<LocatorGeocodeResult> doInBackground(LocatorFindParameters... params) {
			// Perform routing request on background thread
			mException = null;
			List<LocatorGeocodeResult> results = null;

			// Create locator using default online geocoding service and tell it to find the given address
			Locator locator = Locator.createOnlineLocator(getString(R.string.geocodeservice_url));
			try {
				results = locator.find(params[0]);
			} catch (Exception e) {
				mException = e;
			}
			return results;
		}

		@Override
		protected void onPostExecute(final List<LocatorGeocodeResult> result) {
			// Display results on UI thread
			mProgressDialog.dismiss();
			if (mException != null) {
				Log.w(TAG, "LocatorSyncTask failed with:");
				mException.printStackTrace();
				Toast.makeText(getActivity(),getString(R.string.addressSearchFailed),Toast.LENGTH_LONG).show();
				return;
			}

			if (result.size() == 0) {
				Toast.makeText(getActivity(),getString(R.string.noResultsFound), Toast.LENGTH_LONG).show();
			} else {

						int icono;
						if(DAY_MAP.equals(mBasemapPortalItemId)){
							icono = R.drawable.pin_circle_purple;
						}else if(NIGHT_MAP.equals(mBasemapPortalItemId) ){
							icono = R.drawable.pin_circle_yellow;
						}else if(ALT_MAP.equals(mBasemapPortalItemId)){
							icono = R.drawable.pin_circle_green;
						}else{
							icono = R.drawable.pin_circle_purple;
						}
						LocatorGeocodeResult geocodeResult = result.get(0);
						// get return geometry from geocode result
						Point resultPoint = geocodeResult.getLocation();
						// create marker symbol to represent location
						Drawable drawable = getActivity().getResources().getDrawable(icono);
						PictureMarkerSymbol resultSymbol = new PictureMarkerSymbol(getActivity(), drawable);
						// create graphic object for resulting location
						Graphic resultLocGraphic = new Graphic(resultPoint, resultSymbol);
						// add graphic to location layer
						mLocationLayer.addGraphic(resultLocGraphic);
						// Get the address
						String address = geocodeResult.getAddress();
						mLocationLayerPoint = resultPoint;
						// Zoom map to geocode result location
						mMapView.zoomToResolution(geocodeResult.getLocation(), 0.3);
						showSearchResultLayout(address);


					}

			}
		}

	/**
	 * This class provides an AsyncTask that performs a routing request on a
	 * background thread and displays the resultant route on the map on the UI
	 * thread.
	 */
	private class RouteAsyncTask extends AsyncTask<List<LocatorFindParameters>, Void, RouteResult> {
		private static final String TAG_ROUTE_SEARCH_PROGRESS_DIALOG = "TAG_ROUTE_SEARCH_PROGRESS_DIALOG";
		private Exception mException;
		private ProgressDialogFragment mProgressDialog;
		private List<Point> puntosGlobales;
		private List<String> nombrePuntosGlobales;
		private List<String> nombreEdificios;
		private int tipo;
		private RutaEspecial ruta;
		public RouteAsyncTask() {
		}

		@Override
		protected void onPreExecute() {
			mProgressDialog = ProgressDialogFragment.newInstance(getActivity().getString(R.string.route_search));
			// set the target fragment to receive cancel notification
			mProgressDialog.setTargetFragment(MapFragment.this,REQUEST_CODE_PROGRESS_DIALOG);
			mProgressDialog.show(getActivity().getFragmentManager(),TAG_ROUTE_SEARCH_PROGRESS_DIALOG);
		}

		@Override
		protected RouteResult doInBackground(List<LocatorFindParameters>... params) {
			// Perform routing request on background thread
			mException = null;

            //Declarando clase global
            final GlobalPoints globalVariable;
            globalVariable = (GlobalPoints) getActivity().getApplicationContext();

            // Define route objects
			List<Point> puntos = new ArrayList<Point>();
			List<String> nombrePuntos = new ArrayList<String>();

			List<Point> puntosDeImportancia = new ArrayList<Point>();
			List<String> nombreImportanes = new ArrayList<String>();

			// Create a new locator to geocode start/end points; by default uses ArcGIS online world geocoding service
			Locator locator = Locator.createOnlineLocator(getString(R.string.geocodeservice_url));

			try {
				// Geocode start position, or use My Location (from GPS)
				LocatorFindParameters startParam = params[0].get(0);

				int cantidadPuntos = params[0].size();

				for(int i = 0;i < cantidadPuntos ; i++){

					LocatorFindParameters paramLocator = params[0].get(i);
					Point punto = paramLocator.getLocation();
					List<LocatorGeocodeResult> geocodeResults = null;

					if(punto == null){
						if (paramLocator.getText().equals(getString(R.string.my_location))) {
							if(i == 0){
								mStartLocation = getString(R.string.my_location);
							}
							else if((i+1) == cantidadPuntos){
								mEndLocation = getString(R.string.my_location);
							}

							punto = (Point) GeometryEngine.project(mLocation, mWm,mEgs);

						} else {

							geocodeResults = locator.find(paramLocator);
							punto = geocodeResults.get(0).getLocation();
							if(i==0){
								mStartLocation = geocodeResults.get(0).getAddress();
							}else if((i+1) == cantidadPuntos){
								mEndLocation = geocodeResults.get(0).getAddress();
							}



							if (isCancelled()) {
								return null;
							}
						}

					}else{
						if(i==0){
							mStartLocation = paramLocator.getText();
						}else if((i+1) == cantidadPuntos){
							mEndLocation = paramLocator.getText();
						}
					}


					puntos.add(punto);
					nombrePuntos.add(paramLocator.getText());


					if(!paramLocator.getText().equalsIgnoreCase("NONE")){

						puntosDeImportancia.add(punto);
						nombreImportanes.add(paramLocator.getText());
					}

				}

                List<MapPoint> listaPuntos = new LinkedList<MapPoint>();
				for(int i=0;i<puntos.size();i++){
					MapPoint punto = new MapPoint();
					punto.setName(nombrePuntos.get(i));
					punto.setStartLatitud(puntos.get(i).getX());
					punto.setStartLongitud(puntos.get(i).getY());
					if(i==0){
						if(mStartLocation.equals(getString(R.string.my_location))){
							punto.setName("Origen");
						}else{
							punto.setName(mStartLocation);
						}
					}
					if(i+1==puntos.size()){
						if(mEndLocation.equals(getString(R.string.my_location))){
							punto.setName("Destino");
						}else{
							punto.setName(mEndLocation);
						}
					}
					listaPuntos.add(punto);
				}
				globalVariable.setListaPuntos(listaPuntos);
				Log.d("Punto Inicio", "Nombre: " + mStartLocation + "\nPuntoX: " + puntos.get(0).getX() + "\nPuntoY: " + puntos.get(0).getY());
				Log.d("Punto Fin", "Nombre: " + mEndLocation + "\nPuntoX: " + puntos.get(puntos.size() - 1).getX() + "\nPuntoY: " + puntos.get(puntos.size() - 1).getY());

				puntosGlobales = puntosDeImportancia;
				nombrePuntosGlobales = nombreImportanes;

			} catch (Exception e) {
				mException = e;
				return null;
			}
			if (isCancelled()) {
				return null;
			}

			// Create a new routing task pointing to an ArcGIS Network Analysis Service
			RouteTask routeTask;
			RouteParameters routeParams = null;
			try {
				routeTask = RouteTask.createOnlineRouteTask(getString(R.string.routingservice_url), null);
				// Retrieve default routing parameters
				routeParams = routeTask.retrieveDefaultRouteTaskParameters();
			} catch (Exception e) {
				mException = e;
				return null;
			}
			if (isCancelled()) {
				return null;
			}

			// Customize the route parameters
			NAFeaturesAsFeature routeFAF = new NAFeaturesAsFeature();
			List<StopGraphic> stopGraphics = new ArrayList<StopGraphic>();
			for (Point p : puntos){
				StopGraphic sg = new StopGraphic(p);
				stopGraphics.add(sg);
			}
			Graphic[] graphics = stopGraphics.toArray(new Graphic[stopGraphics.size()]);

			routeFAF.setFeatures(graphics);
			routeFAF.setCompressedRequest(true);
			routeParams.setStops(routeFAF);
			//noinspection ResourceType
			routeParams.setOutSpatialReference(mMapView.getSpatialReference());

			// Solve the route
			RouteResult routeResult;
			try {
				routeResult = routeTask.solve(routeParams);
			} catch (Exception e) {
				mException = e;
				return null;
			}
			if (isCancelled()) {
				return null;
			}
			return routeResult;
		}

		@Override
		protected void onPostExecute(RouteResult result) {
			// Display results on UI thread
			mProgressDialog.dismiss();
			if (mException != null) {
				Log.w(TAG, "RouteSyncTask failed with:");
				mException.printStackTrace();
				Toast.makeText(getActivity(),getString(R.string.routingFailed), Toast.LENGTH_LONG).show();
				return;
			}

			// Get first item in list of routes provided by server
			Route route = result.getRoutes().get(0);

			int linea;
			if(DAY_MAP.equals(mBasemapPortalItemId)){
				linea = Color.rgb(106,0,143);
			}else if(NIGHT_MAP.equals(mBasemapPortalItemId) ){
				linea = Color.rgb(255,255,102);
			}else if(ALT_MAP.equals(mBasemapPortalItemId)){
				linea = Color.rgb(34,139,34);
			}else{
				linea = Color.rgb(106,0,143);
			}

			// Create polyline graphic of the full route
			SimpleLineSymbol lineSymbol = new SimpleLineSymbol(linea, 5,STYLE.SOLID);

			Graphic routeGraphic = new Graphic(route.getRouteGraphic().getGeometry(), lineSymbol);

			// Create point graphic to mark start of route
			Point startPoint = ((Polyline) routeGraphic.getGeometry()).getPoint(0);
			Graphic startGraphic = createMarkerGraphic(startPoint, 0);

			int letra;
			if(DAY_MAP.equals(mBasemapPortalItemId)){
				letra = Color.BLACK;
			}else if(NIGHT_MAP.equals(mBasemapPortalItemId) ){
				//letra = Color.WHITE;
				letra = Color.rgb(255,140,0);
			}else if(ALT_MAP.equals(mBasemapPortalItemId)){
				letra = Color.BLACK;
			}else{
				letra = Color.BLACK;
			}

			TextSymbol textoInicial = new TextSymbol(FontStyle.ITALIC.name(),nombrePuntosGlobales.get(0),letra);
			if(NIGHT_MAP.equals(mBasemapPortalItemId)){
				textoInicial.setFontDecoration(FontDecoration.UNDERLINE);
				//textoInicial.setFontFamily("serif");

			}
			textoInicial.setSize(20);
			textoInicial.setFontWeight(FontWeight.BOLD);
			textoInicial.setHorizontalAlignment(TextSymbol.HorizontalAlignment.CENTER);
			textoInicial.setOffsetY(40);


			List<Graphic> graphics = new ArrayList<Graphic>();
			graphics.add(routeGraphic);
			graphics.add(startGraphic);
			graphics.add(new Graphic(startPoint,textoInicial));

			for(int i = 1;i < puntosGlobales.size() - 1 ;i++){

				graphics.add(createMarkerGraphic(puntosGlobales.get(i),1));

				TextSymbol text = new TextSymbol(FontStyle.ITALIC.name(),nombrePuntosGlobales.get(i),letra);
				if(NIGHT_MAP.equals(mBasemapPortalItemId)){
					text.setFontDecoration(FontDecoration.UNDERLINE);
					//textoInicial.setFontFamily("serif");

				}
				text.setSize(20);
				text.setFontWeight(FontWeight.BOLD);
				text.setHorizontalAlignment(TextSymbol.HorizontalAlignment.CENTER);
				text.setOffsetY(40);

				graphics.add(new Graphic(puntosGlobales.get(i),text));
			}

			// Create point graphic to mark end of route
			int endPointIndex = ((Polyline) routeGraphic.getGeometry()).getPointCount() - 1;
			Point endPoint = ((Polyline) routeGraphic.getGeometry()).getPoint(endPointIndex);
			Graphic endGraphic = createMarkerGraphic(endPoint, 2);


			TextSymbol textoFinal = new TextSymbol(FontStyle.ITALIC.name(),nombrePuntosGlobales.get(nombrePuntosGlobales.size() -1),letra);
			if(NIGHT_MAP.equals(mBasemapPortalItemId)){
				textoFinal.setFontDecoration(FontDecoration.UNDERLINE);
				//textoInicial.setFontFamily("serif");

			}
			textoFinal.setSize(20);
			textoFinal.setFontWeight(FontWeight.BOLD);
			textoFinal.setHorizontalAlignment(TextSymbol.HorizontalAlignment.CENTER);
			textoFinal.setOffsetY(40);
			graphics.add(endGraphic);
			graphics.add(new Graphic(endPoint,textoFinal));

			// Add these graphics to route layer
			mRouteLayer.addGraphics(graphics.toArray(new Graphic [graphics.size()] ));

			// Zoom to the extent of the entire route with a padding
			mMapView.setExtent(route.getEnvelope(),100);

			// Save routing directions so user can display them later
			List<RouteDirection> lista = route.getRoutingDirections();
			LinkedList<RouteDirection> listaL = new LinkedList<RouteDirection>();
			listaL.addAll(lista);
			mRoutingDirections = listaL;

			if(editButton != null){editButton.setVisible(true);}
			// Show Routing Result Layout
			showRoutingResultLayout(route.getTotalMiles(),tipo,ruta,nombreEdificios);

		}

		Graphic createMarkerGraphic(Point point, int pointType) {

			int icono;
			if(DAY_MAP.equals(mBasemapPortalItemId)){
				icono = R.drawable.pin_circle_purple;
			}else if(NIGHT_MAP.equals(mBasemapPortalItemId) ){
				icono = R.drawable.pin_circle_yellow;
			}else if(ALT_MAP.equals(mBasemapPortalItemId)){
				icono = R.drawable.pin_circle_green;
			}else{
				icono = R.drawable.pin_circle_purple;
			}

			Drawable marker = null;
			if(pointType == 0){
				marker = getResources().getDrawable(R.drawable.pin_circle_red);
			} else if(pointType == 1) {
				marker = getResources().getDrawable(icono);
			} else if(pointType == 2){
				marker = getResources().getDrawable(R.drawable.pin_circle_blue);
			}

			PictureMarkerSymbol destinationSymbol = new PictureMarkerSymbol(
					mMapView.getContext(), marker);


			float offsetY = convertPixelsToDp(getActivity(), marker.getBounds().bottom);
			destinationSymbol.setOffsetY(offsetY);

			return new Graphic(point, destinationSymbol);
		}

		public List<String> getNombreEdificios() {
			return nombreEdificios;
		}

		public void setNombreEdificios(List<String> nombreEdificios) {
			this.nombreEdificios = nombreEdificios;
		}

		public int getTipo() {
			return tipo;
		}

		public void setTipo(int tipo) {
			this.tipo = tipo;
		}

		public RutaEspecial getRuta() {
			return ruta;
		}

		public void setRuta(RutaEspecial ruta) {
			this.ruta = ruta;
		}
	}

	/**
	 * This class provides an AsyncTask that performs a reverse geocoding
	 * request on a background thread and displays the resultant point on the
	 * map on the UI thread.
	 */
	public class ReverseGeocodingAsyncTask extends AsyncTask<Point, Void, LocatorReverseGeocodeResult> {
		private static final String TAG_REVERSE_GEOCODING_PROGRESS_DIALOG = "TAG_REVERSE_GEOCODING_PROGRESS_DIALOG";
		private Exception mException;
		private ProgressDialogFragment mProgressDialog;
		private Point mPoint;

		@Override
		protected void onPreExecute() {
			mProgressDialog = ProgressDialogFragment.newInstance(getActivity().getString(R.string.reverse_geocoding));
			// set the target fragment to receive cancel notification
			mProgressDialog.setTargetFragment(MapFragment.this,REQUEST_CODE_PROGRESS_DIALOG);
			mProgressDialog.show(getActivity().getFragmentManager(),TAG_REVERSE_GEOCODING_PROGRESS_DIALOG);
		}

		@Override
		protected LocatorReverseGeocodeResult doInBackground(Point... params) {
			// Perform reverse geocoding request on background thread
			mException = null;
			LocatorReverseGeocodeResult result = null;
			mPoint = params[0];

			// Create locator using default online geocoding service and tell it to find the given point
			Locator locator = Locator.createOnlineLocator(getString(R.string.geocodeservice_url));
			try {
				// Our input and output spatial reference will be the same as the map
				@SuppressWarnings("ResourceType") SpatialReference mapRef = mMapView.getSpatialReference();
				result = locator.reverseGeocode(mPoint, 100.0, mapRef, mapRef);
				mLocationLayerPoint = mPoint;

			} catch (Exception e) {
				mException = e;
			}
			// return the resulting point(s)
			return result;
		}

		@Override
		protected void onPostExecute(LocatorReverseGeocodeResult result) {


			// Display results on UI thread
			mProgressDialog.dismiss();
			if (mException != null) {
				Log.w(TAG, "LocatorSyncTask failed with:");
				mException.printStackTrace();
				Toast.makeText(getActivity(),getString(R.string.addressSearchFailed),Toast.LENGTH_LONG).show();
				return;
			}

			// Construct a nicely formatted address from the results
			StringBuilder address = new StringBuilder();
			if (result != null && result.getAddressFields() != null) {
				Map<String, String> addressFields = result.getAddressFields();
				address.append(String.format("%s\n", addressFields.get("SingleKey")));
				//se utiliza la funcion de localizar desde busqueda avanzada, para marcar el punto
				//justamente en el edificio, y no en terreno fuera de lugar
				onAdvanceSearchLocate(address.toString());
			}
		}
	}

	/**
	 * Converts device specific pixels to density independent pixels.
	 *
	 * @param context
	 * @param px
	 *            number of device specific pixels
	 * @return number of density independent pixels
	 */
	private float convertPixelsToDp(Context context, float px) {
		DisplayMetrics metrics = context.getResources().getDisplayMetrics();
		float dp = px / (metrics.densityDpi / 160f);
		return dp;
	}

    /**
     * Changes the String Values for the Menu.
     */

    public String getChangeSoundFlag() {
        return mSoundActive;
    }

    public void setChangeSoundFlag(String mSoundActive) {
        this.mSoundActive = mSoundActive;
    }

	public void drawRoute(){
		onGetRoute(getString(R.string.my_location), mLocationLayerPointString);
	}



	private class SitioAsyncTask extends AsyncTask<List<String>, Void, List<String>>{

		private Exception mException;

		private volleySingleton volley;
		private RequestQueue requestQueue;
		private List<String> listaSitios1 = new ArrayList<>();

		@Override
		protected List<String> doInBackground(List<String>... lists) {
			//asignamos valores al volley y a la queue.
			mException=null;
			volley = volleySingleton.getInstance(getActivity().getApplicationContext());
			requestQueue = volley.getRequestQueue();
			//llamamos a getSitios, donde obtenemos las cosas que necesitamos
			getSitios();

			return listaSitios1;
		}

		@Override
		protected void onPreExecute() {

		}




		protected void onPostExecute(List<String> list){

		}

		public void getSitios() {

			String url = Constantes.GET_SITIOS2;
			//creamos un object request, y lo añadimos a la cola
			JsonObjectRequest request = new JsonObjectRequest
					(Request.Method.GET, url,null, new Response.Listener<JSONObject>() {
						@Override
						public void onResponse(JSONObject response) {
							//cuando obtenemos una respuesta, la procesamos
							procesarRespuesta(response);
						}
					}, new Response.ErrorListener() {
						@Override
						public void onErrorResponse(VolleyError error) {
							Toast.makeText(getActivity().getApplicationContext(),"Se produjo un error: "+ error,Toast.LENGTH_LONG).show();
						}
					});
			addtoQueue(request);
		}

		private void procesarRespuesta(JSONObject response) {
			try {
				// Obtener atributo "estado"
				String estado = response.getString("estado");
				switch (estado) {
					case "1": // EXITO
						// Obtener array "sitios" Json
						JSONArray arraySitios = response.getJSONArray("edificio");
						// Parsear
						for (int i = 0; i < arraySitios.length(); i++) {
							//como se obtiene un arreglo, se guarda cada sitio en una lista
							JSONObject sitio = (JSONObject) arraySitios.get(i);

							String nombreEdificio = sitio.getString("NOMBRE");
							listaSitios1.add(nombreEdificio);
						}
						sitios = listaSitios1;


						ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(),R.layout.support_simple_spinner_dropdown_item,sitios);
						//autoCompleteTextView.setLines(1);

						autoCompleteTextView.setAdapter(adapter);


						break;
					case "2": // FALLIDO
						String mensaje2 = response.getString("mensaje");
						Toast.makeText(getActivity().getApplicationContext(),"Lo sentimos, "+ mensaje2,Toast.LENGTH_LONG).show();
						break;
				}

			} catch (JSONException e) {
				e.printStackTrace();
			}


		}


		public void addtoQueue(Request request) {
			if (request != null) {
				request.setTag(this);
				if (requestQueue == null)
					requestQueue = volley.getRequestQueue();
				request.setRetryPolicy(new DefaultRetryPolicy(
						600000, 3, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
				));

				requestQueue.add(request);
			}
		}
	}
}