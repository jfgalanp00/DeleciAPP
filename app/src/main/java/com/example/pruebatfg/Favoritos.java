package com.example.pruebatfg;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pruebatfg.adapter.RestauranteFavoritoAdapter;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class Favoritos extends AppCompatActivity {
    private RecyclerView recyclerView;
    private RestauranteFavoritoAdapter adapter;
    private List<String> itemList;
    ImageButton imgBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favoritos);

        imgBack = findViewById(R.id.imgBackFAV);

        Intent intent = getIntent();
        String usuario = intent.getStringExtra("usuario");

        recyclerView = findViewById(R.id.vistaReciclador);
        itemList = new ArrayList<>();
        adapter = new RestauranteFavoritoAdapter(itemList, usuario);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        getItemList();

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

    private void getItemList() {
        // Limpiar la lista existente
        itemList.clear();

        Intent intent = getIntent();
        String usuario = intent.getStringExtra("usuario");

        // Obtén una referencia a la colección "usuario" y al documento específico
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        //DocumentReference userRef = db.collection("usuario").document("pm6glCqPEBSgsDLsjgH6");
        DocumentReference userRef = db.collection("usuario").document(buscarUsuarioPorNombre(usuario));

        // Obtiene los datos del documento
        userRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if (documentSnapshot.exists()) {
                    // Verifica si el campo "favoritos" existe en el documento
                    if (documentSnapshot.contains("favoritos")) {
                        List<String> favoritos = (List<String>) documentSnapshot.get("favoritos");
                        itemList.addAll(favoritos);
                        adapter.notifyDataSetChanged();

                        // Ajusta la altura del RecyclerView para mostrar todos los elementos consecutivamente
                        setRecyclerViewHeight();

                        // Agrega logs para verificar los elementos de itemList
                        for (String item : itemList) {
                            Log.e("Favoritos", "Elemento: " + item);
                        }

                        Log.e("Favoritos", "Cantidad de elementos en itemList: " + itemList.size());
                    }
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.e("Favoritos", "Error al obtener datos de Firestore: " + e.getMessage());
            }
        });
    }

    private void setRecyclerViewHeight() {
        int itemHeight = getResources().getDimensionPixelSize(R.dimen.item_height); // Altura de cada elemento
        int itemCount = itemList.size(); // Número de elementos en la lista
        int recyclerViewHeight = itemHeight * itemCount;

        recyclerView.getLayoutParams().height = recyclerViewHeight;
        recyclerView.requestLayout();
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