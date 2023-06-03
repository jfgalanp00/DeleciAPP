package com.example.pruebatfg;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class Busqueda extends AppCompatActivity {
    Spinner mySp, mySpPrecio;
    Button btBuscar;
    ImageButton btCambiarUsuario, btnfavoritos, btnlogo;
    TextView txtBienvenido;
    private FirebaseFirestore db;
    private ArrayList<String> lista = new ArrayList<>();
    private ArrayList<String> listaPrecio = new ArrayList<>();
    private String usuario;
    private String nombre;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_busqueda);

        mySp = findViewById(R.id.miSpinner);
        mySpPrecio = findViewById(R.id.mySpinnerPrecios);
        btBuscar = findViewById(R.id.btnBuscar);
        btCambiarUsuario = findViewById(R.id.imgUsuario);
        btnfavoritos = findViewById(R.id.imgFavorito);
        btnlogo = findViewById(R.id.imgBtnBackLogo);

        db = FirebaseFirestore.getInstance();
        txtBienvenido = findViewById(R.id.txtBienvenido);

        Intent intent = getIntent();
        nombre = intent.getStringExtra("nombre");
        txtBienvenido.setText("Bienvenido " + nombre + " !");

        usuario = intent.getStringExtra("usuario");

        mostarSpinner();
    }

    private void mostarSpinner() {
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, androidx.appcompat.R.layout.support_simple_spinner_dropdown_item, lista);

        mySp.setAdapter(adapter);

        //aqui busca la coleccion de la base de datos
        CollectionReference collectionRef = db.collection("tipocomida");

        // Agregar un listener para obtener los datos de la colección
        collectionRef.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                if (error != null) {
                    Log.e("Firestore", "Error al obtener datos: " + error.getMessage());
                    return;
                }

                // Limpiar los datos anteriores
                lista.clear();

                // Recorrer los documentos y agregar los datos al Spinner
                for (DocumentSnapshot document : value) {
                    // Obtener el valor deseado del documento, por ejemplo, el campo "nombre"
                    String nombre = document.getString("tipo");

                    if (nombre != null) {
                        lista.add(nombre);
                    }
                }

                // Notificar al adaptador que los datos han cambiado
                adapter.notifyDataSetChanged();
            }
        });

        ArrayAdapter<String> adapterPrecio = new ArrayAdapter<>(this, androidx.appcompat.R.layout.support_simple_spinner_dropdown_item, listaPrecio);

        mySpPrecio.setAdapter(adapterPrecio);

        //aqui busca la coleccion de la base de datos
        collectionRef = db.collection("precio");

        // Agregar un listener para obtener los datos de la colección
        collectionRef.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                if (error != null) {
                    Log.e("Firestore", "Error al obtener datos: " + error.getMessage());
                    return;
                }

                // Limpiar los datos anteriores
                listaPrecio.clear();

                // Recorrer los documentos y agregar los datos al Spinner
                for (DocumentSnapshot document : value) {
                    // Obtener el valor deseado del documento, por ejemplo, el campo "nombre"
                    String precio = document.getString("price");

                    if (precio != null) {
                        listaPrecio.add(precio);
                    }
                }

                // Notificar al adaptador que los datos han cambiado
                adapterPrecio.notifyDataSetChanged();
            }
        });

        btBuscar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String tipoComida = mySp.getSelectedItem().toString();
                String precio = mySpPrecio.getSelectedItem().toString();

                Intent intento = new Intent(Busqueda.this, Encuentra.class);
                intento.putExtra("tipocomida", tipoComida);
                intento.putExtra("precio", precio);
                intento.putExtra("usuario", usuario);
                startActivity(intento);
            }
        });

        btCambiarUsuario.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ObjectAnimator animator = ObjectAnimator.ofFloat(btCambiarUsuario, "rotation", 0f, 360f);
                animator.setDuration(1000);
                animator.setInterpolator(new AccelerateDecelerateInterpolator());
                animator.addListener(new Animator.AnimatorListener() {
                    @Override
                    public void onAnimationStart(Animator animation) {
                        // Acción a realizar al comenzar la animación (opcional)
                        btCambiarUsuario.setRotation(0f);
                    }

                    @Override
                    public void onAnimationEnd(Animator animation) {
                        // Acción a realizar al finalizar la animación

                        Intent intento = new Intent(Busqueda.this, ModificarUsuario.class);
                        intento.putExtra("nombre", nombre);
                        intento.putExtra("usuario", usuario);
                        startActivity(intento);
                    }

                    @Override
                    public void onAnimationCancel(Animator animation) {
                        // Acción a realizar si la animación se cancela (opcional)
                    }

                    @Override
                    public void onAnimationRepeat(Animator animation) {
                        // Acción a realizar si la animación se repite (opcional)
                    }
                });
                animator.start();
            }
        });

        btnfavoritos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ObjectAnimator animator = ObjectAnimator.ofFloat(btnfavoritos, "rotation", 0f, 360f);
                animator.setDuration(1000);
                animator.setInterpolator(new AccelerateDecelerateInterpolator());
                animator.addListener(new Animator.AnimatorListener() {
                    @Override
                    public void onAnimationStart(Animator animation) {
                        // Acción a realizar al comenzar la animación (opcional)
                        btnfavoritos.setRotation(0f);
                    }

                    @Override
                    public void onAnimationEnd(Animator animation) {
                        // Acción a realizar al finalizar la animación
                        Intent intento = new Intent(Busqueda.this, Favoritos.class);
                        intento.putExtra("usuario", usuario);
                        startActivity(intento);
                    }

                    @Override
                    public void onAnimationCancel(Animator animation) {
                        // Acción a realizar si la animación se cancela (opcional)
                    }

                    @Override
                    public void onAnimationRepeat(Animator animation) {
                        // Acción a realizar si la animación se repite (opcional)
                    }
                });
                animator.start();
            }

        });

        btnlogo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ObjectAnimator animator = ObjectAnimator.ofFloat(btnlogo, "rotation", 0f, 360f);
                animator.setDuration(1000);
                animator.setInterpolator(new AccelerateDecelerateInterpolator());
                animator.addListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        super.onAnimationEnd(animation);
                        btnlogo.setRotation(0f);
                    }
                });
                animator.start();
            }
        });
    }
}