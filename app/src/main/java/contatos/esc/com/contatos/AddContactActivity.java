package contatos.esc.com.contatos;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import contatos.esc.com.contatos.api.ApiClient;
import contatos.esc.com.contatos.api.ApiContactInterface;
import contatos.esc.com.contatos.model.Contato;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AddContactActivity extends AppCompatActivity {

    private EditText etId;
    private EditText etName;
    private EditText etFone;

    private Bundle extras;

    private Integer idContact;

    private ProgressDialog mProgressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_contact);

        etId = (EditText) findViewById(R.id.id);
        etName = (EditText) findViewById(R.id.name);
        etFone = (EditText) findViewById(R.id.fone);

        extras = getIntent().getExtras();

        if (extras != null) {
            idContact = Integer.parseInt(extras.getString("id"));
            setTitle("Editar contato");
            etId.setEnabled(false);
            getContact(idContact);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (extras != null) {
            getMenuInflater().inflate(R.menu.menu_edit, menu);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_delete) {
            confirmDeleteContact();
        }

        return super.onOptionsItemSelected(item);
    }

    public void getContact(int id) {
        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setIndeterminate(true);
        mProgressDialog.setMessage("Carregando...");
        mProgressDialog.show();

        ApiContactInterface apiService = ApiClient.getClient().create(ApiContactInterface.class);
        Call<Contato> call = apiService.getById(id);
        call.enqueue(new Callback<Contato>() {
            @Override
            public void onResponse(Call<Contato> call, Response<Contato> response) {
                if (mProgressDialog.isShowing())
                    mProgressDialog.dismiss();
                if (response.isSuccessful()) {
                    Contato c = response.body();
                    etId.setText(c.getId().toString());
                    etName.setText(c.getName());
                    etFone.setText(c.getFone());
                } else {
                    Toast.makeText(getApplicationContext(), "Resposta não foi sucesso", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Contato> call, Throwable t) {
                if (mProgressDialog.isShowing())
                    mProgressDialog.dismiss();
                Log.e("TAG", t.getMessage());
            }
        });
    }

    public void onClickSaveContact(View view) {
        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setIndeterminate(true);
        mProgressDialog.setMessage("Carregando...");
        mProgressDialog.show();

        ApiContactInterface apiService = ApiClient.getClient().create(ApiContactInterface.class);

        String name = etName.getText().toString();
        String fone = etFone.getText().toString();

        if (idContact != null) {
            Call<Contato> call = apiService.update(idContact, name, fone);
            call.enqueue(new Callback<Contato>() {
                @Override
                public void onResponse(Call<Contato> call, Response<Contato> response) {
                    if (mProgressDialog.isShowing())
                        mProgressDialog.dismiss();
                    if (response.isSuccessful()) {
                        Toast.makeText(getApplicationContext(), "Contato alterado com sucesso.", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(getApplicationContext(), "Erro ao cadastrar contato. Erro no servidor", Toast.LENGTH_SHORT).show();
                        ResponseBody errorBody = response.errorBody();
                        Log.e("TAG", errorBody.toString());
                    }
                }

                @Override
                public void onFailure(Call<Contato> call, Throwable t) {
                    if (mProgressDialog.isShowing())
                        mProgressDialog.dismiss();
                    Log.e("TAG", t.getMessage());
                }
            });
        } else {
            Call<Contato> call = apiService.create(name, fone);
            call.enqueue(new Callback<Contato>() {
                @Override
                public void onResponse(Call<Contato> call, Response<Contato> response) {
                    if (mProgressDialog.isShowing())
                        mProgressDialog.dismiss();
                    if (response.isSuccessful()) {
                        Toast.makeText(getApplicationContext(), "Contato cadastrado com sucesso.", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(getApplicationContext(), "Erro ao cadastrar contato. Erro no servidor", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<Contato> call, Throwable t) {
                    if (mProgressDialog.isShowing())
                        mProgressDialog.dismiss();
                    Log.e("TAG", t.getMessage());
                }
            });
        }
        finish();
    }

    public void deleteContact(int id) {
        ApiContactInterface apiService = ApiClient.getClient().create(ApiContactInterface.class);
        Call call = apiService.delete(id);
        call.enqueue(new Callback<Contato>() {
            @Override
            public void onResponse(Call call, Response response) {
                if (response.isSuccessful()) {
                    Toast.makeText(AddContactActivity.this, "Contato excluído com sucesso.", Toast.LENGTH_SHORT).show();
                    Intent i = new Intent(getApplicationContext(), MainActivity.class);
                    startActivity(i);
                } else {
                    Toast.makeText(getApplicationContext(), "Resposta não foi sucesso", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call call, Throwable t) {
                Log.e("TAG", t.getMessage());
            }
        });
    }

    private void confirmDeleteContact() {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setMessage("Deseja excluir o contato?");

        alertDialogBuilder.setPositiveButton("Sim",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {
                        deleteContact(idContact);
                    }
                });

        alertDialogBuilder.setNegativeButton("Não",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {

                    }
                });

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }
}
