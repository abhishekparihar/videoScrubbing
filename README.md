Video-ImageView-Demo
====================
This is a demo application to use the video file stored in the memory and extract the frames based on the time factor. The extracted frame is shown on the image view in the application .
The app contain the seekBar that is used to fetch different frames at different point of time .

The app coding is a four step process :
Step 1 :  Adding the video to the external sdCard in emulator .
Step 2 :  Creating the layout with imageView and SeekBar .
Step 3 :  Fetching the file from the external sdCard.
Step 4 :  Extracting the frame from the video .
____________________________________________________________________________________________________________________________________________________________
Step 1 : Adding the video to the external sdCard in emulator .

In Eclipse goto 
Windows → Open Prespectives → DDMS → File Explorer → mnt → sdcard .
Now on the right hand side corner you have a button with a red arrow pointing to a device.
Press the button and select the Video file you want to store in the emulator.

DONE. :)

____________________________________________________________________________________________________________________________________________________________
Step 2 :  Creating the layout with imageView and SeekBar . “activity_main.xml”

<LinearLayout xmlns:android="http://schemas.android.com/apk/res/android"
    android:layout_width="match_parent"
    android:layout_height="match_parent" 
    android:orientation="vertical">

    <ImageView
        android:id="@+id/imageView"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content" />

    <SeekBar
        android:id="@+id/frameScroll"
        android:layout_width="match_parent"
        android:layout_height="wrap_content" />

</LinearLayout>


____________________________________________________________________________________________________________________________________________________________
Step 3 : Fetching the file from the external sdCard.

There are few lines that are needed to be understood before you code.

o   To fetch a file from the sdcard you need to have the path of the video file .For this there is a function provided . “Environment.getExternalStorageDirectory()” .This directly  gets the path till the sdcard . Now if your video is stored in the sdcard itself then you have to give just the file name else you  have to mention the full path from sdcard onward . 

File sdcard = Environment.getExternalStorageDirectory();
File file = new File(sdcard, "I still have a soul (HBO Boxing).mp4");


o   Once you get the Media file you have to get the data ie. the frames and the embaded data from the media . That can be fetched by the object of “MediaMetadataRetriever” class.

MediaMetadataRetriever mediaRetriever;
mediaRetriever = new MediaMetadataRetriever();
mediaRetriever.setDataSource(file.getAbsolutePath());

o   To get the length of the video you need to have the object  of “MediaPlayer” class.
MediaPlayer mp = MediaPlayer.create(this,Uri.parse(file.getAbsolutePath()));
int duration = mp.getDuration();

just to be clear “Uri.parse(file.getAbsolutePath())” return the string value which is the complete path of the video file. In my case it is  : “/mnt/sdcard/myVideo.mp4”

____________________________________________________________________________________________________________________________________________________________
Step 4 : Extracting the frame from the video .

Bitmap bitmap;
bitmap = mediaRetriever.getFrameAtTime(3000); 
