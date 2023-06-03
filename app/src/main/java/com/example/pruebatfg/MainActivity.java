package com.example.pruebatfg;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;


public class MainActivity extends AppCompatActivity {

    Button btnRegistro, btnEntrar;
    EditText edUsuario, edClave;
    ImageButton imgButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnRegistro = findViewById(R.id.btnRegistrar);
        btnEntrar = findViewById(R.id.btnEntrar);
        imgButton = findViewById(R.id.imgBtnBackLogo);
        edUsuario = findViewById(R.id.ediUsuario);
        edClave = findViewById(R.id.ediClave);

        comprobarCredenciales();

        btnRegistro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intetnto = new Intent(MainActivity.this, Registra.class);
                startActivity(intetnto);
            }
        });

        imgButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ObjectAnimator animator = ObjectAnimator.ofFloat(imgButton, "rotation", 0f, 360f);
                animator.setDuration(1000);
                animator.setInterpolator(new AccelerateDecelerateInterpolator());
                animator.addListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        super.onAnimationEnd(animation);
                        imgButton.setRotation(0f);
                    }
                });
                animator.start();
                edUsuario.setText("");
                edClave.setText("");
            }
        });
    }

    public void comprobarCredenciales() {
        btnEntrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String nombreUsuario = edUsuario.getText().toString().trim();
                String clave = edClave.getText().toString().trim();

                if (nombreUsuario.isEmpty() || clave.isEmpty()) {
                    // Mostrar un mensaje de error si alguno de los campos está vacío
                    Toast.makeText(MainActivity.this, "Por favor, ingresa un nombre de usuario y una contraseña", Toast.LENGTH_SHORT).show();
                } else {
                    FirebaseFirestore db = FirebaseFirestore.getInstance();
                    CollectionReference usuariosRef = db.collection("usuario");

                    Query query = usuariosRef.whereEqualTo("usuario", nombreUsuario)
                            .whereEqualTo("clave", clave);

                    query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {
                                QuerySnapshot querySnapshot = task.getResult();
                                if (!querySnapshot.isEmpty()) {
                                    DocumentSnapshot document = querySnapshot.getDocuments().get(0);
                                    String nombre = document.getString("nombre");


                                    // Continuar con la lógica despues de la verificacion
                                    //pasar documentos de la coleccion usuario
                                    Intent intent = new Intent(MainActivity.this, Busqueda.class);
                                    intent.putExtra("nombre", nombre);
                                    intent.putExtra("usuario", nombreUsuario);
                                    startActivity(intent);
                                } else {
                                    // Usuario no encontrado en la base de datos Firestore o la clave no coincide
                                    Toast.makeText(MainActivity.this, "Nombre de usuario o contraseña incorrectos", Toast.LENGTH_SHORT).show();
                                }
                            } else {
                                // Error al obtener los datos de Firestore
                                Toast.makeText(MainActivity.this, "Error al obtener los datos del usuario", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            }
        });
    }
}