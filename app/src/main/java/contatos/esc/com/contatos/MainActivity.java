package contatos.esc.com.contatos;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import contatos.esc.com.contatos.api.ApiClient;
import contatos.esc.com.contatos.api.ApiContactInterface;
import contatos.esc.com.contatos.model.Contato;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity implements ListView.OnItemClickListener {

    private ProgressDialog mProgressDialog;
    private ListView listContacts;
    private FloatingActionButton fab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        fab = (FloatingActionButton) findViewById(R.id.fab);
        listContacts = (ListView) findViewById(R.id.listContacts);
        listContacts.setOnItemClickListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        getAllContacts();
    }

    public void getAllContacts() {
        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setIndeterminate(true);
        mProgressDialog.setMessage("Carregando...");
        mProgressDialog.show();

        ApiContactInterface apiService = ApiClient.getClient().create(ApiContactInterface.class);

        Call<List<Contato>> call = apiService.getAll();
        call.enqueue(new Callback<List<Contato>>() {
            @Override
            public void onResponse(Call<List<Contato>> call, Response<List<Contato>> response) {
                if (mProgressDialog.isShowing())
                    mProgressDialog.dismiss();

                if (response.isSuccessful()) {
                    List<Contato> contatos = response.body();
                    ArrayList<HashMap<String, String>> list = new ArrayList<HashMap<String, String>>();
                    if (contatos != null) {
                        for (Contato c : contatos) {
                            int id = c.getId();
                            String name = c.getName();

                            HashMap<String, String> contacts = new HashMap<>();
                            contacts.put("id", "" + id);
                            contacts.put("name", name);
                            list.add(contacts);
                        }

                        ListAdapter adapter = new SimpleAdapter(
                                MainActivity.this, list, R.layout.list_item,
                                new String[]{"id", "name"},
                                new int[]{R.id.id, R.id.name});

                        listContacts.setAdapter(adapter);
                    } else {
                        Toast.makeText(getApplicationContext(), "Nenhum contato cadastrado", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(getApplicationContext(), "Resposta não foi sucesso", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Contato>> call, Throwable t) {
                Log.e("TAG", t.getMessage());
                Toast.makeText(MainActivity.this, "Servidor não está respondendo", Toast.LENGTH_SHORT).show();
                if (mProgressDialog.isShowing())
                    mProgressDialog.dismiss();
            }
        });
    }

    public void onClickAddContact(View v) {
        Intent i = new Intent(this, AddContactActivity.class);
        startActivity(i);
        //overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Intent intent = new Intent(this, AddContactActivity.class);
        HashMap<String, String> map = (HashMap) parent.getItemAtPosition(position);
        String empId = map.get("id").toString();
        intent.putExtra("id", empId);
        startActivity(intent);
        //overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }
}
