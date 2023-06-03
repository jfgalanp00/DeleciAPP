package com.example.pruebatfg;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

public class ModificarUsuario extends AppCompatActivity {
    ImageButton imgBack;
    Button btnAplicar, btnBorrar;
    EditText edNombre, edUsuario, edClaveActual, edClaveNueva, edRepiteClave;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_modificar_usuario_fav);

        imgBack = findViewById(R.id.imgBackFAV);
        btnAplicar = findViewById(R.id.btnAplicarMU);
        btnBorrar = findViewById(R.id.borrarMU);
        edNombre = findViewById(R.id.edNombreMU);
        edUsuario = findViewById(R.id.edNombreUsuarioMU);
        edClaveActual = findViewById(R.id.edClaveActualMU);
        edClaveNueva = findViewById(R.id.edNuevaClaveMU);
        edRepiteClave = findViewById(R.id.edRepiteClaveMU);

        Intent intent = getIntent();
        String nombre = intent.getStringExtra("nombre");
        String usuario = intent.getStringExtra("usuario");
        edNombre.setText( nombre );
        edUsuario.setText( usuario );

        btnBorrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                edNombre.setText("");
                edUsuario.setText("");
                edClaveActual.setText("");
                edClaveNueva.setText("");
                edRepiteClave.setText("");
            }
        });

        btnAplicar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                confirmacionDeDatos();
            }
        });

        imgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ObjectAnimator animator = ObjectAnimator.ofFloat(imgBack, "rotation", 0f, 360f);
                animator.setDuration(1000);
                animator.setInterpolator(new AccelerateDecelerateInterpolator());
                animator.addListener(new Animator.AnimatorListener() {
                    @Override
                    public void onAnimationStart(Animator animation) {
                        // Acción a realizar al comenzar la animación (opcional)
                        imgBack.setRotation(0f);
                    }

                    @Override
                    public void onAnimationEnd(Animator animation) {
                        // Acción a realizar al finalizar la animación
                        onBackPressed();
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
    }

    private void confirmacionDeDatos() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Confirmación");
        builder.setMessage("¿Deseas Aplicar Estos Datos?");

        builder.setPositiveButton("Si", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Accion para aplicar datos
                //aqui tenemos que hacer update a todos los campos que se cambian en la bd
                //si los campos no estan vacios que cambie el contenido que hay escrito
                Intent intent = getIntent();
                String usuario = intent.getStringExtra("usuario");
                FirebaseFirestore db = FirebaseFirestore.getInstance();
                DocumentReference userRef = db.collection("usuario").document(buscarUsuarioPorNombre(usuario));
                userRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                            @Override
                            public void onSuccess(DocumentSnapshot documentSnapshot) {
                                String claveActualBD = documentSnapshot.getString("clave");
                                String claveActualInput = edClaveActual.getText().toString();

                                String nombre = edNombre.getText().toString();
                                String usuario = edUsuario.getText().toString();
                                if(!nombre.isEmpty() && !usuario.isEmpty()){
                                    userRef.update(
                                            "nombre", nombre,
                                            "usuario", usuario
                                    ).addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            Toast.makeText(ModificarUsuario.this, "Se han almacenado correctamente los cambios", Toast.LENGTH_SHORT).show();
                                        }
                                    }).addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Toast.makeText(ModificarUsuario.this, "Error al almacenar los cambios", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                }else{
                                    Toast.makeText(ModificarUsuario.this, "El campo nombre o nombre usuario estan VACIOS", Toast.LENGTH_SHORT).show();
                                }

                                // Comprobar si la clave actual ingresada coincide con la clave almacenada en Firestore
                                if (claveActualBD.equals(claveActualInput)) {
                                    // La clave actual es correcta, continuar con la verificación de la nueva clave
                                    String claveNueva = edClaveNueva.getText().toString();
                                    String repiteClave = edRepiteClave.getText().toString();

                                    // Comprobar si la nueva clave coincide con la repetición de la clave
                                    if (claveNueva.equals(repiteClave)) {
                                        // La nueva clave coincide con la repetición de la clave, realizar la actualización en Firestore
                                        userRef.update("clave", claveNueva)
                                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                    @Override
                                                    public void onSuccess(Void aVoid) {
                                                        Toast.makeText(ModificarUsuario.this, "Se han almacenado correctamente", Toast.LENGTH_SHORT).show();
                                                    }
                                                })
                                                .addOnFailureListener(new OnFailureListener() {
                                                    @Override
                                                    public void onFailure(@NonNull Exception e) {
                                                        Toast.makeText(ModificarUsuario.this, "Error al almacenar los datos", Toast.LENGTH_SHORT).show();
                                                    }
                                                });

                                    } else {
                                        // La nueva clave y la repetición de la clave no coinciden
                                        Toast.makeText(ModificarUsuario.this, "La nueva clave y la repetición de la clave no coinciden", Toast.LENGTH_SHORT).show();
                                    }
                                } else {
                                    // La clave actual ingresada no coincide con la clave almacenada en Firestore
                                    Toast.makeText(ModificarUsuario.this, "La clave actual ingresada no es correcta", Toast.LENGTH_SHORT).show();
                                }
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(ModificarUsuario.this, "Error al obtener los datos del usuario", Toast.LENGTH_SHORT).show();
                            }
                        });
            }
        });

        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Accion continuar en la aplicacion
                dialog.dismiss();
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    //aqui lo que hacemos es buscar por nombre de usuario y recoger el documento del usuario y podemos acceder a cada usuario
    private String buscarUsuarioPorNombre(String nombreUsuario) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference usuariosRef = db.collection("usuario");

        Query query = usuariosRef.whereEqualTo("usuario", nombreUsuario).limit(1);

        Task<QuerySnapshot> task = query.get();
        while (!task.isComplete()) {
            // Espera a que se complete la tarea
        }

        if (task.isSuccessful()) {
            QuerySnapshot querySnapshot = task.getResult();
            if (!querySnapshot.isEmpty()) {
                DocumentSnapshot documentSnapshot = querySnapshot.getDocuments().get(0);
                return documentSnapshot.getId();
            }
        } else {
            Exception exception = task.getException();
            Log.e("Error", "Error al buscar el usuario por nombre", exception);
        }

        return null; // No se encontró el usuario
    }
}