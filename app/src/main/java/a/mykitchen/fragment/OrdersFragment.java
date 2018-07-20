package a.mykitchen.fragment;

import a.mykitchen.R;
import a.mykitchen.items.OrderViewItem;
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
import com.my.backery.backend.domain.Order;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import java.util.LinkedList;
import java.util.List;

public class OrdersFragment extends Fragment {
    private ListView orderItemsList;
    private ListView ordersList;
    private MappingJackson2HttpMessageConverter converter;
    private Context context;

    public void setConverter(MappingJackson2HttpMessageConverter converter) {
        this.converter = converter;
    }

    public void setContext(Context context) { this.context = context;}

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_orders, container, false);
        orderItemsList = view.findViewById(R.id.orderItemsList);
//        orderButton = view.findViewById(R.id.orderButton);
//        orderButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                new PostOrderTask(getString(R.string.service_base_url), items).execute();
//            }
//        });
        ordersList = view.findViewById(R.id.ordersList);

        updateOrders(getString(R.string.service_base_url));
        return view;
    }

    protected void updateOrders(String url) {
        new GetOrdersTask(url).execute();
    }

    private class OrdersListAdapter extends ArrayAdapter<Order> {
        private int layout;

        public OrdersListAdapter(Context context, int resource, Order[] objects) {
            super(context, resource, objects);
            layout = resource;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                OrderViewItem item =
                        new OrderViewItem(getContext(), parent, layout, getItem(position));
                convertView = item.getView();
                convertView.setTag(item);
            }

            return convertView;
        }

    }

    public class GetOrdersTask extends AsyncTask<Void, Void, Order[]> {
        private static final String ORDER_URL = "order";

        private String baseUrl;
        private List<Exception> exceptions = new LinkedList<>();

        public GetOrdersTask(String baseUrl) {
            this.baseUrl = baseUrl;
        }

        @Override
        protected Order[] doInBackground(Void... voids) {
            RestTemplate restTemplate = new RestTemplate();

            try {
                restTemplate.getMessageConverters().add(converter);

                Order[] orders;

                orders = restTemplate.getForObject(getUrl(), Order[].class);
                return orders;
            }catch(Exception e) {
                exceptions.add(e);
            }
            return null;
        }

        @Override
        protected void onPostExecute(Order[] orders) {
            if (!exceptions.isEmpty())
                Toast.makeText(context, getString(R.string.FAILED_TO_FETCH_ORDERS) + exceptions.get(0).getMessage(),Toast.LENGTH_LONG).show();
            else
                setOrders(orders);
        }


        private String getUrl() {
            return new StringBuilder().append(baseUrl).append("/").append(ORDER_URL).toString();
        }
    }

    protected void setOrders(Order[] orders) {
        ordersList.setAdapter(new OrdersListAdapter(context, R.layout.order_item, orders));
    }
}
