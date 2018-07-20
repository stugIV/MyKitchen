package a.mykitchen.items;

import a.mykitchen.R;
import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.my.backery.backend.domain.OrderItem;

import java.text.SimpleDateFormat;

public class QueueViewItem {
    private View view;

    private TextView name;
    private TextView due;

    public QueueViewItem(Context context, ViewGroup parent, int layout, OrderItem orderItem) {
        LayoutInflater inflater = LayoutInflater.from(context);
        view = inflater.inflate(layout, parent, false);

        name = view.findViewById(R.id.queue_item_name);
        due = view.findViewById(R.id.queue_item_due);

        SimpleDateFormat f = new SimpleDateFormat("hh:mm");
        name.setText(orderItem.getMenuItem().getItemName());
        due.setText(f.format(orderItem.getOrder().getDue()));
        view.setBackgroundColor(Color.RED);
    }

    public View getView() {
        return view;
    }
}
