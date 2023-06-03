package com.example.pruebatfg;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.ImageButton;

import com.example.pruebatfg.adapter.RestauranteAdapter;
import com.example.pruebatfg.modelo.Restaurante;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

public class Encuentra extends AppCompatActivity {
    ImageButton imgBack;
    RecyclerView myRecycler;
    RestauranteAdapter myAdapter;
    FirebaseFirestore myFirestore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_encuentra);

        myFirestore = FirebaseFirestore.getInstance();
        myRecycler = findViewById(R.id.MyRecicler);
        myRecycler.setLayoutManager(new LinearLayoutManager(this));

        Intent intent = getIntent();
        String tipoComida = intent.getStringExtra("tipocomida");
        String precio = intent.getStringExtra("precio");
        String usuario = intent.getStringExtra("usuario");

        Query query = myFirestore.collection("restaurante")
                .whereEqualTo("tipocomida", tipoComida)
                .whereEqualTo("precio", precio);

        FirestoreRecyclerOptions<Restaurante> firebaseOptions = new FirestoreRecyclerOptions.Builder<Restaurante>().setQuery(query, Restaurante.class).build();

        myAdapter = new RestauranteAdapter(firebaseOptions, usuario);
        myAdapter.notifyDataSetChanged();
        myRecycler.setAdapter(myAdapter);

        imgBack = findViewById(R.id.imgBtnBack);

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

    @Override
    protected void onStart() {
        super.onStart();
        myAdapter.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
        myAdapter.stopListening();
    }
}
