/**
 * Copyright 2012 Facebook
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.facebook.irons;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import tw.com.irons.try_case2.R;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.facebook.FacebookActivity;
import com.facebook.GraphObject;
import com.facebook.GraphUser;
import com.facebook.HttpMethod;
import com.facebook.LoginButton;
import com.facebook.ProfilePictureView;
import com.facebook.Request;
import com.facebook.Response;
import com.facebook.SessionLoginBehavior;
import com.facebook.SessionState;
import com.facebook.android.AsyncFacebookRunner.RequestListener;
import com.facebook.android.FacebookError;
import com.facebook.android.Util;

public class HelloFacebookSampleActivity extends FacebookActivity {
    @SuppressWarnings("serial")
    private static final List<String> PERMISSIONS = new ArrayList<String>() {{
        add("publish_actions");
    }};

    private final int PICK_FRIENDS_ACTIVITY = 1;
    private final int PICK_PLACE_ACTIVITY = 2;
    private final int REAUTHORIZE_ACTIVITY = 3;
    private final String APP_ID = "132547436896177";
    private final String PENDING_ACTION_BUNDLE_KEY = "com.facebook.samples.hellofacebook:PendingAction";

    private Button postStatusUpdateButton;
    private Button postPhotoButton;
    private Button pickFriendsButton;
    private Button pickPlaceButton;
    private LoginButton loginButton;
    private ProfilePictureView profilePictureView;
    private TextView greeting;
    private PendingAction pendingAction = PendingAction.NONE;
    private final Location SEATTLE_LOCATION = new Location("") {
        {
            setLatitude(47.6097);
            setLongitude(-122.3331);
        }
    };
    private GraphUser user;

    private enum PendingAction {
        NONE,
        POST_PHOTO,
        POST_STATUS_UPDATE
    }
    
    String message;
	String title ;
	String content ;
	String time ;
	String imagePath;
	String realImagePath;
	Bitmap image;
	Button postPhotoAndContentButton;
    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fb);

		Intent intent = getIntent();
		Bundle bundle = new Bundle();
		bundle = intent.getExtras();
		title = bundle.getString("title");
		content = bundle.getString("content");
		time = bundle.getString("time");
		imagePath = bundle.getString("imagePath");
		realImagePath = bundle.getString("realImagePath");
		
        loginButton = (LoginButton) findViewById(R.id.login_button);
        loginButton.setApplicationId(APP_ID);
        loginButton.setUserInfoChangedCallback(new LoginButton.UserInfoChangedCallback() {
            @Override
            public void onUserInfoFetched(GraphUser user) {
                HelloFacebookSampleActivity.this.user = user;
                updateUI();
                // It's possible that we were waiting for this.user to be populated in order to post a
                // status update.
                handlePendingAction();
            }
        });
        
        Button button = (Button)findViewById(R.id.btn1);
        button.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(HelloFacebookSampleActivity.this, App.class);
				startActivity(intent);
			}
		});
        
        Button button2 = (Button)findViewById(R.id.btn2);
        button2.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(HelloFacebookSampleActivity.this, App.class);
				startActivity(intent);
			}
		});

		
        profilePictureView = (ProfilePictureView) findViewById(R.id.profilePicture);
        greeting = (TextView) findViewById(R.id.greeting);

        postStatusUpdateButton = (Button) findViewById(R.id.postStatusUpdateButton);
        postStatusUpdateButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
            	

        		
        		AlertDialog alert = null;
        
        		final EditText et = new EditText(HelloFacebookSampleActivity.this);
  
        				
        		et.setText("��O���D:"+title+"�C��O���e:"+content+"�C����ɶ�"+time);
        		
        		
        		AlertDialog.Builder builder = new AlertDialog.Builder(HelloFacebookSampleActivity.this);
        		builder.setTitle("���ɸ�T");
        		builder.setView(et);
        		builder.setCancelable(true);
        		
        		
        		builder.setPositiveButton("�T�w", 
        				new DialogInterface.OnClickListener(){
        			@Override
        			public void onClick(DialogInterface dialog, int which) {
        				message = et.getText().toString();
        				onClickPostStatusUpdate();
        			}
        			
        		});

        		builder.setNegativeButton("���", 
        				new DialogInterface.OnClickListener(){
        			@Override
        			public void onClick(DialogInterface dialog, int which) {
        				
        		        
        			}			
        		});

        		alert = builder.create();
        		alert.show();
          
            }
        });

        postPhotoButton = (Button) findViewById(R.id.postPhotoButton);
        postPhotoButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                onClickPostPhoto();
            }
        });

        pickFriendsButton = (Button) findViewById(R.id.pickFriendsButton);
        pickFriendsButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                onClickPickFriends();
            }
        });

        pickPlaceButton = (Button) findViewById(R.id.pickPlaceButton);
        pickPlaceButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                onClickPickPlace();
            }
        });
        
        postPhotoAndContentButton=(Button)findViewById(R.id.postPhotoAndContentButton);
        postPhotoAndContentButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				AlertDialog alert = null;
	    		LayoutInflater dialog = LayoutInflater.from(HelloFacebookSampleActivity.this);
	    		View dview = dialog.inflate(R.layout.fb_sendphoto, null);
	    		final ImageView dailyImage = (ImageView)dview.findViewById(R.id.dailyImage);
	    		final Button changeToDailyImage = (Button)dview.findViewById(R.id.button1);
	    		final Button changeToPhoneImage = (Button)dview.findViewById(R.id.button2);
	    		image = BitmapFactory.decodeFile(imagePath);
	    		final EditText et=(EditText)dview.findViewById(R.id.editText2);
	    		dailyImage.setImageBitmap(image);
	    		et.setText("��O���D:"+title+"�C��O���e:"+content+"�C����ɶ�"+time);
	    		changeToDailyImage.setEnabled(false);
	    		
	    		AlertDialog.Builder builder = new AlertDialog.Builder(HelloFacebookSampleActivity.this);
	    		builder.setTitle("���ɸ�T");
	    		builder.setView(dview);
	    		builder.setCancelable(true);
	    		
	    		
	    		builder.setPositiveButton("�T�w", 
	    				new DialogInterface.OnClickListener(){
	    			@Override
	    			public void onClick(DialogInterface dialog, int which) {
	    				//onClickPostStatusUpdate();
	    				message = et.getText().toString();
	    				
	    				Bundle bundle = new Bundle();
	    				//bundle.putString("description", message);
	    				//bundle.putString("message", message);
	    				//bundle.putString("name", message);
	    				bundle.putString("caption", message);
	    				/*
	    			  	Request request = Request
	    	                    .newStatusUpdateRequest(com.facebook.Session.getActiveSession(), message, new Request.Callback() {
	    	                        @Override
	    	                        public void onCompleted(Response response) {
	    	                            showAlert(message, response.getGraphObject(), response.getError());
	    	                        }
	    	                    });*/
	    				/*
	    	             Request request2 = Request.newUploadPhotoRequest(com.facebook.Session.getActiveSession(), image, new Request.Callback() {
	    	    		            @Override
	    	    		            public void onCompleted(Response response) {
	    	    		                showAlert("Photo Post", response.getGraphObject(), response.getError());
	    	    		            }
	    	    		        });*/
	    	             //request2.setParameters(bundle);
	    	             //request2.set
	    	             
	    	             //Request request3 = new Request() ;
	    	             //Request.newUploadPhotoRequest(com.facebook.Session.getActiveSession(), image, null).setParameters(bundle);
	    	            //Request.executeBatchAsync(request2);
	    	            //RequestBatch batch = new 
	    				
	    				
	    				  Bitmap bmp = BitmapFactory.decodeFile(imagePath);
		    				// �w��Ӫ��ۤ�O�b�b SD �d�W, �ҥH�N SD �d���eŪ�X��@ bytes[]
		    				     ByteArrayOutputStream baos = new ByteArrayOutputStream();
		    				     bmp.compress(Bitmap.CompressFormat.PNG, 100, baos);
		    				     final byte[] data = baos.toByteArray();
	    				Bundle params = new Bundle();
	    				params.putString("message", message);                
	    				params.putByteArray("picture", data); //bytes contains photo bytes, no problem here
	    			
	    	            //Request res = new Request(com.facebook.Session.getActiveSession(), "me/feed", params, "POST");
	    	            Request r = new Request(com.facebook.Session.getActiveSession(), "me/photos");
	    	            r.setParameters(params);r.setHttpMethod(HttpMethod.POST);r.setCallback(new Request.Callback() {
	    	    		            @Override
	    	    		            public void onCompleted(Response response) {
	    	    		                showAlert("Photo Post", response.getGraphObject(), response.getError());
	    	    		            }
	    	    		        });
	    	            Request.executeBatchAsync(r);
/*
	    	             Facebook facebook = new Facebook(APP_ID);
	    	             String wallAlbumID = null;
	    	             String response = facebook.request("me/albums");
	    	             JSONObject json = Util.parseJson(response);
	    	             JSONArray albums = json.getJSONArray("data");
	    	             for (int i =0; i < albums.length(); i++) {
	    	                 JSONObject album = albums.getJSONObject(i);                     
	    	                 if (album.getString("type").equalsIgnoreCase("wall")) {
	    	                     wallAlbumID = album.getString("id");
	    	                     Log.d("JSON", wallAlbumID);
	    	                     break;
	    	                 }
	    	             }
	    	             
	    	             if (wallAlbumID != null) {
	    	                 Bundle params = new Bundle();
	    	                 params.putString("message", "Uploaded on " + now());                
	    	                 params.putByteArray("source", bytes);
	    	                 asyncRunner.request(wallAlbumID+"/photos", params, "POST", new PostPhotoRequestListener(), null);
	    	             }
	    				*/
	    				/*
	    				  Bitmap bmp = BitmapFactory.decodeFile(imagePath);
	    				// �w��Ӫ��ۤ�O�b�b SD �d�W, �ҥH�N SD �d���eŪ�X��@ bytes[]
	    				     ByteArrayOutputStream baos = new ByteArrayOutputStream();
	    				     bmp.compress(Bitmap.CompressFormat.PNG, 100, baos);
	    				     final byte[] data = baos.toByteArray();
	    				     
	    				     final Facebook mFacebook = new Facebook(APP_ID);
	    				     final String[] PERMISSIONS = new String[] {
	    				     "publish_stream", "read_stream", "offline_access","user_photos" };
	    				      
	    				      
	    				     final AsyncFacebookRunner mAsyncRunner = new AsyncFacebookRunner(mFacebook);
	    				                             mFacebook.authorize(HelloFacebookSampleActivity.this,
	    				                                      PERMISSIONS, new DialogListener() {
																
																@Override
																public void onFacebookError(FacebookError e) {
																	// TODO Auto-generated method stub
																	
																}
																
																@Override
																public void onError(DialogError e) {
																	// TODO Auto-generated method stub
																	
																}
																
																@Override
																public void onComplete(Bundle values) {
																	// TODO Auto-generated method stub

											    				     
											    				     String wallAlbumID = null;
											    				     String response;
																	try {
																		
																		Bundle p = new Bundle();
																		p.putString("name","me" );
																		p.putString("message","me2" );
																		mAsyncRunner.request("me/albums", p, "POST", null ,null);
																		
																		response = mFacebook.request("me/albums");
																		 JSONObject json = Util.parseJson(response);
												    				     JSONArray albums = json.getJSONArray("data");
												    				     for (int i =0; i < albums.length(); i++) {
												    				         JSONObject album = albums.getJSONObject(i);                     
												    				         if (album.getString("type").equalsIgnoreCase("wall")) {
												    				             wallAlbumID = album.getString("id");
												    				             Log.d("JSON", wallAlbumID);
												    				             break;
												    				         }
												    				     }
																	} catch (FacebookError e) {
																		// TODO Auto-generated catch block
																		e.printStackTrace();
																	} catch (JSONException e) {
																		// TODO Auto-generated catch block
																		e.printStackTrace();
																	} catch (MalformedURLException e) {
																		// TODO Auto-generated catch block
																		e.printStackTrace();
																	} catch (IOException e) {
																		// TODO Auto-generated catch block
																		e.printStackTrace();
																	}
											    				    
											    				         
											    				     Request res = new Request(session, graphPath, parameters, httpMethod)
											    				     res.exe
											    				     
											    				     if (wallAlbumID != null) {
																	     Bundle params = new Bundle();
												    				     params.putString("message", "Uploaded on ");
												    				     params.putString("caption", "Uploaded ona ");
												    				     params.putByteArray("source", data); //bytes contains photo bytes, no problem here
												    				     mAsyncRunner.request(wallAlbumID+"/photos", params, "POST", null ,null);
												    				     
											    				         Bundle params = new Bundle();
											    				         params.putString("message", "Uploaded on " + now());                
											    				         params.putByteArray("source", bytes);
											    				         asyncRunner.request(wallAlbumID+"/photos", params, "POST", new PostPhotoRequestListener(), null);
											    				         
											    				     }
																}
																
																@Override
																public void onCancel() {
																	// TODO Auto-generated method stub
																	
																}
															});*/
	    				
	    				/*
	    				final Facebook facebook = new Facebook(APP_ID);
	    			    // postPhotoToWall(facebook.getAccessToken());
	    		        facebook.authorize(HelloFacebookSampleActivity.this,
	    		            new String[] { "publish_stream" },
	    		            new DialogListener() {
	    		                @Override
	    		                public void onFacebookError(FacebookError e) {
	    		                    // TODO Auto-generated method stub
	    		                    Toast.makeText(getApplicationContext(),
	    		                    e.getMessage(), Toast.LENGTH_LONG).show();
	    		                }

	    		                @Override
	    		                public void onError(DialogError dialogError) {
	    		                    // TODO Auto-generated method stub
	    		                    Toast.makeText(getApplicationContext(),
	    		                    dialogError.getMessage(),
	    		                    Toast.LENGTH_LONG).show();
	    		                }

	    		                @Override
	    		                public void onComplete(Bundle values) {
	    		                    postToWall(values.getString(Facebook.TOKEN));
	    		                }

	    		                private void postToWall(String accessToken) {
	    		                    // Toast.makeText(getApplicationContext(),
	    		                    // "trying", Toast.LENGTH_LONG).show();
	    		                    byte[] data = null;
	    		                    Bitmap bi = BitmapFactory.decodeResource(
	    		                        getResources(), 
	    		                        R.drawable.ic_launcher
	    		                    );
	    		                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
	    		                    bi.compress(Bitmap.CompressFormat.JPEG, 100, baos);
	    		                    data = baos.toByteArray();
	    		                    Bundle params = new Bundle();
	    		                    // if (facebook.getAccessToken() != null)
	    		                    params.putString(Facebook.TOKEN,
	    		                        facebook.getAccessToken()
	    		                    );

	    		                    params.putString("method", "photos.upload");
	    		                    params.putString("caption", "www.samplelink.com");
	    		                    // params.putString("message",
	    		                    // "www.google.com");

	    		                    params.putByteArray("picture", data);
	    		                    AsyncFacebookRunner mAsyncRunner = new AsyncFacebookRunner(facebook);
	    		                    mAsyncRunner.request(null, params, "POST",
	    		                        new SampleUploadListener(), null);
	    		                    }

	    		                    @Override
	    		                    public void onCancel() {
	    		                        // TODO Auto-generated method stub
	    		                    }
	    		                }
	    		            );
	    				   */  
	    				/*
	    				   Bundle params = new Bundle();
	    				    params.putString("name", "Facebook SDK for Android");
	    				    params.putString("caption", "Build great social apps and get more installs.");
	    				    params.putString("description", "The Facebook SDK for Android makes it easier and faster to develop Facebook integrated Android apps.");
	    				    params.putString("link", "https://developers.facebook.com/android");
	    				    params.putString("picture", "https://raw.github.com/fbsamples/ios-3.x-howtos/master/Images/iossdk_logo.png");

	    				    WebDialog feedDialog = (
	    				        new WebDialog.FeedDialogBuilder(getActivity(),
	    				            Session.getActiveSession(),
	    				            params))
	    				        .setOnCompleteListener(new OnCompleteListener() {
	    				            @Override
	    				            public void onComplete(Bundle values,
	    				                FacebookException error) {
	    				                // When the story is posted, echo the success
	    				                // and the post Id.
	    				                final String postId = values.getString("post_id");
	    				                if (postId != null) {
	    				                    Toast.makeText(getActivity(),
	    				                        "Posted story, id: "+postId,
	    				                    Toast.LENGTH_SHORT).show();
	    				                }
	    				            }

	    				        })
	    				        .build();
	    				    feedDialog.show();*/
	    			}
	    			
	    		});

	    		builder.setNegativeButton("���", 
	    				new DialogInterface.OnClickListener(){
	    			@Override
	    			public void onClick(DialogInterface dialog, int which) {
	    				
	    		        return;
	    			}			
	    		});

	    		alert = builder.create();
	    		alert.show();
	        
	    		changeToDailyImage.setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						changeToPhoneImage.setEnabled(true);
						image = BitmapFactory.decodeFile(imagePath);
						dailyImage.setImageBitmap(image);
					}
				});
	    		
	    		changeToPhoneImage.setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						changeToDailyImage.setEnabled(true);
						image = BitmapFactory.decodeFile(realImagePath);
						dailyImage.setImageBitmap(image);
					}
				});
	    		

			}
		});
    }

    @Override
    protected void onStart() {
        super.onStart();

        updateUI();

        IntentFilter filter = new IntentFilter();
        filter.addAction(com.facebook.Session.ACTION_ACTIVE_SESSION_OPENED);
        filter.addAction(com.facebook.Session.ACTION_ACTIVE_SESSION_CLOSED);
        LocalBroadcastManager.getInstance(this).registerReceiver(receiver, filter);
    }

    @Override
    protected void onStop() {
        super.onStop();

        LocalBroadcastManager.getInstance(this).unregisterReceiver(receiver);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putInt(PENDING_ACTION_BUNDLE_KEY, pendingAction.ordinal());
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        int ordinal = savedInstanceState.getInt(PENDING_ACTION_BUNDLE_KEY, 0);
        pendingAction = PendingAction.values()[ordinal];
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

 
               
        
    }

    @Override
    protected void onSessionStateChange(SessionState state, Exception exception) {
        super.onSessionStateChange(state, exception);
        if (state == SessionState.OPENED_TOKEN_UPDATED) {
            handlePendingAction();
        }
    }

    private void updateUI() {
        boolean enableButtons = com.facebook.Session.getActiveSession() != null &&
        		com.facebook.Session.getActiveSession().getState().isOpened();

        postStatusUpdateButton.setEnabled(enableButtons);
        postPhotoButton.setEnabled(enableButtons);
        pickFriendsButton.setEnabled(enableButtons);
        pickPlaceButton.setEnabled(enableButtons);

        if (enableButtons && user != null) {
            profilePictureView.setUserId(user.getId());
            greeting.setText(String.format("Hello %s!", user.getFirstName()));
        } else {
            profilePictureView.setUserId(null);
            greeting.setText(null);
        }
    }

    @SuppressWarnings("incomplete-switch")
    private void handlePendingAction() {
        switch (pendingAction) {
            case POST_PHOTO:
                postPhoto();
                break;
            case POST_STATUS_UPDATE:
                postStatusUpdate();
                break;
        }
        pendingAction = PendingAction.NONE;
    }

    private interface GraphObjectWithId extends GraphObject {
        String getId();
    }

    private void showAlert(String message, GraphObject result, Exception exception) {
        String title = null;
        String alertMessage = null;
        if (exception == null) {
            title = "Success";
            String id = result.cast(GraphObjectWithId.class).getId();
            alertMessage = String.format("Successfully posted '%s'.\nPost ID: %s", message, id);
        } else {
            title = "Error";
            alertMessage = exception.getMessage();
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(title).setMessage(alertMessage).setPositiveButton("OK", null);
        builder.show();
    }

    private void onClickPostStatusUpdate() {
        performPublish(PendingAction.POST_STATUS_UPDATE);
    }

    private void postStatusUpdate() {
        if (user != null) {
           // final String message = String
            //        .format("Updating status for %s at %s", user.getFirstName(), (new Date().toString()));
        	
        	Request request = Request
                    .newStatusUpdateRequest(com.facebook.Session.getActiveSession(), message, new Request.Callback() {
                        @Override
                        public void onCompleted(Response response) {
                            showAlert(message, response.getGraphObject(), response.getError());
                        }
                    });
            Request.executeBatchAsync(request);
        } else {
            pendingAction = PendingAction.POST_STATUS_UPDATE;
        }
    }

    private void onClickPostPhoto() {
        performPublish(PendingAction.POST_PHOTO);
    }

    private void postPhoto() {
    	
    		//Button button=(Button)findViewById(R.id.)
  
    		AlertDialog alert = null;
    		LayoutInflater dialog = LayoutInflater.from(this);
    		View dview = dialog.inflate(R.layout.fb_sendphoto, null);
    		final ImageView dailyImage = (ImageView)dview.findViewById(R.id.dailyImage);
    		final Button changeToDailyImage = (Button)dview.findViewById(R.id.button1);
    		final Button changeToPhoneImage = (Button)dview.findViewById(R.id.button2);
    		image = BitmapFactory.decodeFile(imagePath);
    		if(image==null){
    			Builder builder = new Builder(this);
    			builder.setMessage("����O�S���ۤ�");
    			builder.setPositiveButton("�T�w", null);
    			builder.show();
    			return;
    		}
    		dailyImage.setImageBitmap(image);
    		//et.setText("��O���D:"+title+"�C��O���e:"+content+"�C����ɶ�"+time);
    		changeToDailyImage.setEnabled(false);
    		
    		AlertDialog.Builder builder = new AlertDialog.Builder(HelloFacebookSampleActivity.this);
    		builder.setTitle("���ɸ�T");
    		builder.setView(dview);
    		builder.setCancelable(true);
    		
    		
    		builder.setPositiveButton("�T�w", 
    				new DialogInterface.OnClickListener(){
    			@Override
    			public void onClick(DialogInterface dialog, int which) {
    				//onClickPostStatusUpdate();
    		        Request request = Request.newUploadPhotoRequest(com.facebook.Session.getActiveSession(), image, new Request.Callback() {
    		            @Override
    		            public void onCompleted(Response response) {
    		                showAlert("Photo Post", response.getGraphObject(), response.getError());
    		            }
    		        });
    		        Request.executeBatchAsync(request);
    			}
    			
    		});

    		builder.setNegativeButton("���", 
    				new DialogInterface.OnClickListener(){
    			@Override
    			public void onClick(DialogInterface dialog, int which) {
    				
    		        return;
    			}			
    		});

    		alert = builder.create();
    		alert.show();
        
    		changeToDailyImage.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					changeToPhoneImage.setEnabled(true);
					image = BitmapFactory.decodeFile(imagePath);
					dailyImage.setImageBitmap(image);
				}
			});
    		
    		changeToPhoneImage.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					changeToDailyImage.setEnabled(true);
					image = BitmapFactory.decodeFile(realImagePath);
					dailyImage.setImageBitmap(image);
				}
			});
    		

    }

    private void onClickPickFriends() {
        //Intent intent = new Intent(this, PickFriendsActivity.class);
        //startActivityForResult(intent, PICK_FRIENDS_ACTIVITY);
    }

    private void onClickPickPlace() {
        //Intent intent = new Intent(this, PickPlaceActivity.class);
        //PickPlaceActivity.populateParameters(intent, SEATTLE_LOCATION, null);
        //startActivityForResult(intent, PICK_PLACE_ACTIVITY);
    }

    private void performPublish(PendingAction action) {
    	com.facebook.Session session = com.facebook.Session.getActiveSession();
        if (session != null) {
            pendingAction = action;
            if (session.getPermissions().contains("publish_actions")) {
                // We can do the action right away.
                handlePendingAction();
            } else {
                // We need to reauthorize, then complete the action when we get called back.
            	com.facebook.Session.ReauthorizeRequest reauthRequest = new com.facebook.Session.ReauthorizeRequest(this, PERMISSIONS).
                        setRequestCode(REAUTHORIZE_ACTIVITY).
                        setLoginBehavior(SessionLoginBehavior.SSO_WITH_FALLBACK);
                session.reauthorizeForPublish(reauthRequest);
            }
        }
    }

    private BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            updateUI();
        }
    };

    public class SampleUploadListener extends BaseRequestListener {
    	  public void onComplete(final String response, final Object state) {
    	   try {
    	    // process the response here: (executed in background thread)
    	    Log.d("Facebook-Example", "Response: " + response.toString());
    	    JSONObject json = Util.parseJson(response);
    	    final String src = json.getString("src");
    	    // then post the processed result back to the UI thread
    	    // if we do not do this, an runtime exception will be generated
    	    // e.g. "CalledFromWrongThreadException: Only the original
    	    // thread that created a view hierarchy can touch its views."
    	   } catch (JSONException e) {
    	    Log.w("Facebook-Example", "JSON Error in response");
    	   } catch (FacebookError e) {
    	    Log.w("Facebook-Example", "Facebook Error: " + e.getMessage());
    	   }
    	  }
    	  public void onFacebookError(FacebookError e, Object state) {
    	   // TODO Auto-generated method stub
    	  }
    	 }

    	// �o�O�ǻ��?�� CLASS. �b��SDK EXAMPLE �����.

    	 public abstract class BaseRequestListener implements RequestListener {
    	  public void onFacebookError(FacebookError e, final Object state) {
    	   Log.e("Facebook", e.getMessage());
    	   e.printStackTrace();
    	  }
    	  public void onFileNotFoundException(FileNotFoundException e,
    	    final Object state) {
    	   Log.e("Facebook", e.getMessage());
    	   e.printStackTrace();
    	  }
    	  public void onIOException(IOException e, final Object state) {
    	   Log.e("Facebook", e.getMessage());
    	   e.printStackTrace();
    	  }
    	  public void onMalformedURLException(MalformedURLException e,
    	    final Object state) {
    	   Log.e("Facebook", e.getMessage());
    	   e.printStackTrace();
    	  }
    	 }
}
