package a.mykitchen.fragment;

import a.mykitchen.R;
import a.mykitchen.items.QueueViewItem;
import android.app.Fragment;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;
import com.my.backery.backend.domain.OrderItem;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import java.util.LinkedList;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Builder
public class QueueFragment extends Fragment {

    private ListView queueList;
    private MappingJackson2HttpMessageConverter converter;
    private Context context;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.queue_fragment, null);

        queueList = view.findViewById(R.id.queueList);
        updateQueue(getString(R.string.service_base_url));
        return view;

    }

    protected void updateQueue(String url) {
        new GetQueueTask(url).execute();
    }

    private class QueueListAdapter extends ArrayAdapter<OrderItem> {
        private int layout;

        public QueueListAdapter(Context context, int resource, OrderItem[] objects) {
            super(context, resource, objects);
            layout = resource;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                QueueViewItem item =
                        new QueueViewItem(getContext(), parent, layout, getItem(position));
                convertView = item.getView();
                convertView.setTag(item);
            }

            return convertView;
        }

    }

    public class GetQueueTask extends AsyncTask<Void, Void, OrderItem[]> {
        private static final String ORDER_ITEM_URL = "order_item";

        private String baseUrl;
        private List<Exception> exceptions = new LinkedList<>();

        public GetQueueTask(String baseUrl) {
            this.baseUrl = baseUrl;
        }

        @Override
        protected OrderItem[] doInBackground(Void... voids) {
            RestTemplate restTemplate = new RestTemplate();

            try {
                restTemplate.getMessageConverters().add(converter);

                OrderItem[] orderItems;

                orderItems = restTemplate.getForObject(getUrl(), OrderItem[].class);
                return orderItems;
            }catch(Exception e) {
                exceptions.add(e);
            }
            return null;
        }

        @Override
        protected void onPostExecute(OrderItem[] orderItems) {
            if (!exceptions.isEmpty())
                Toast.makeText(context, getString(R.string.FAILED_TO_FETCH_ORDERS) + exceptions.get(0).getMessage(),Toast.LENGTH_LONG).show();
            else
                setOrderItems(orderItems);
        }


        private String getUrl() {
            return new StringBuilder().append(baseUrl).append("/").append(ORDER_ITEM_URL).toString();
        }
    }

    protected void setOrderItems(OrderItem[] orderItems) {
        queueList.setAdapter(
                new QueueFragment.QueueListAdapter(context, R.layout.queue_item, orderItems));
    }

}
