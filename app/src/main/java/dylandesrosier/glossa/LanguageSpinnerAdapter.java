package dylandesrosier.glossa;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

public class LanguageSpinnerAdapter extends ArrayAdapter<LanguageItem> {
    public LanguageSpinnerAdapter(Context context, ArrayList<LanguageItem> languageList) {
        super(context, 0, languageList);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        return initView(position, convertView, parent, false);
    }

    @Override
    public View getDropDownView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        return initView(position, convertView, parent, true);
    }

    private View initView(int position, View convertView, ViewGroup parent, Boolean isDropDown) {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.language_spinner_row, parent, false);
        }

        ImageView languageImage = convertView.findViewById(R.id.languageImage);
        TextView languageText = convertView.findViewById(R.id.languageText);

        if (isDropDown == false){
            languageText.setTextColor(ContextCompat.getColor(getContext(), R.color.White));
        }

        LanguageItem currentItem = getItem(position);

        if (currentItem != null) {
            languageImage.setImageResource(currentItem.getLanguageImage());
            languageText.setText(currentItem.getLanguageName());
        }

        return convertView;
    }
}
