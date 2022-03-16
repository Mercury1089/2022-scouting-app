package com.mercury1089.scoutingapp2019.utils;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.mercury1089.scoutingapp2019.R;
import com.mercury1089.scoutingapp2019.SettingsActivity;
import static com.mercury1089.scoutingapp2019.utils.GenUtils.padLeftZeros;

public class ListAdapter extends BaseAdapter {
    SettingsActivity context;
    String[] data;
    private static LayoutInflater inflater = null;
    private Dialog loading_alert;
    public final static int QRCodeSize = 500;

    public ListAdapter(Context context, String[] data) {
        // TODO Auto-generated constructor stub
        this.context = (SettingsActivity) context;
        this.data = data;
        inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return data.length;
    }

    @Override
    public Object getItem(int position) {
        // TODO Auto-generated method stub
        return data[position];
    }

    @Override
    public long getItemId(int position) {
        // TODO Auto-generated method stub
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // TODO Auto-generated method stub
        View vi = inflater.inflate(R.layout.qr_list_item, null);

        Button item = vi.findViewById(R.id.itemButton);

        String[] qrData = data[position].split(",");
        Log.d("Stuff", data[position]);
        String scouterName = qrData[0], teamNumber = qrData[1], matchNumber = qrData[2], qrString = data[position];

        item.setText(context.getString(R.string.QRCacheItem, padLeftZeros(teamNumber, 4), padLeftZeros(matchNumber, 2)));
        item.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loading_alert = new Dialog(context);
                loading_alert.requestWindowFeature(Window.FEATURE_NO_TITLE);
                loading_alert.setContentView(R.layout.loading_screen);
                loading_alert.setCancelable(false);
                loading_alert.show();

                ListAdapter.QRRunnable qrRunnable = new ListAdapter.QRRunnable(v.getTag().toString().split("~"), context, v);
                new Thread(qrRunnable).start();
            }
        });
        item.setTag(scouterName + "~" + teamNumber + "~" + matchNumber + "~" + qrString);
        item.setId(Integer.parseInt(teamNumber+matchNumber));
        return vi;
    }

    //QR Generation
    private Bitmap TextToImageEncode(String Value) throws WriterException {
        BitMatrix bitMatrix;
        try {
            bitMatrix = new MultiFormatWriter().encode(
                    Value,
                    BarcodeFormat.DATA_MATRIX.QR_CODE,
                    QRCodeSize, QRCodeSize, null
            );
        } catch (IllegalArgumentException illegalArgumentException) {
            return null;
        }

        int bitMatrixWidth = bitMatrix.getWidth();
        int bitMatrixHeight = bitMatrix.getHeight();
        int[] pixels = new int[bitMatrixWidth * bitMatrixHeight];
        for (int y = 0; y < bitMatrixHeight; y++) {
            int offset = y * bitMatrixWidth;
            for (int x = 0; x < bitMatrixWidth; x++) {
                pixels[offset + x] = bitMatrix.get(x, y) ?
                        GenUtils.getAColor(context, R.color.black) : GenUtils.getAColor(context, R.color.white);
            }
        }

        Bitmap bitmap = Bitmap.createBitmap(bitMatrixWidth, bitMatrixHeight, Bitmap.Config.ARGB_4444);
        bitmap.setPixels(pixels, 0, 500, 0, 0, bitMatrixWidth, bitMatrixHeight);
        return bitmap;
    }

    class QRRunnable implements Runnable {

        private String scouter, teamNum, matchNum, qrString;
        private SettingsActivity context;

        public QRRunnable(String[] qrData, Context c, View v){
            scouter = qrData[0];
            teamNum = qrData[1];
            matchNum = qrData[2];
            qrString = qrData[3];
            context = (SettingsActivity) c;
        }

        @Override
        public void run() {
            try {
                Bitmap bitmap = TextToImageEncode(qrString);
                context.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Dialog dialog = new Dialog(context);
                        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                        dialog.setContentView(R.layout.popup_qr_cached);

                        ImageView imageView = dialog.findViewById(R.id.imageView);
                        TextView scouterName = dialog.findViewById(R.id.ScouterNameQR);
                        TextView teamNumber = dialog.findViewById(R.id.TeamNumberQR);
                        TextView matchNumber = dialog.findViewById(R.id.MatchNumberQR);
                        Button closeButton = dialog.findViewById(R.id.CloseButton);
                        imageView.setImageBitmap(bitmap);

                        dialog.setCancelable(false);

                        scouterName.setText(scouter);
                        teamNumber.setText(GenUtils.padLeftZeros(teamNum, 2));
                        matchNumber.setText(GenUtils.padLeftZeros(matchNum, 2));

                        loading_alert.dismiss();

                        dialog.show();

                        closeButton.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                dialog.dismiss();
                            }
                        });
                    }
                });
            } catch (WriterException e){}
        }
    }
}
