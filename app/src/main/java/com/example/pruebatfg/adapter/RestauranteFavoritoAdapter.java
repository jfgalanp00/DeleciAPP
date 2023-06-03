package com.example.pruebatfg.adapter;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pruebatfg.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.List;

public class RestauranteFavoritoAdapter extends RecyclerView.Adapter<RestauranteFavoritoAdapter.ViewHolder> {
    private List<String> itemList;
    private String nombreUsuario;

    public RestauranteFavoritoAdapter(List<String> itemList, String usuario) {
        this.itemList = itemList;
        this.nombreUsuario = usuario;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.view_restaurante_favorito_single, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        String restaurante = itemList.get(position);
        holder.bind(restaurante);

        holder.imgStar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int itemPosition = holder.getAdapterPosition();
                if (itemPosition != RecyclerView.NO_POSITION) {
                    // Eliminar el restaurante de la lista local
                    itemList.remove(itemPosition);
                    notifyItemRemoved(itemPosition);

                    // Eliminar el restaurante de Firebase Firestore
                    eliminarRestauranteFirestore(restaurante, nombreUsuario);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView textView;
        private ImageButton imgStar;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            textView = itemView.findViewById(R.id.txtNombreFav);
            imgStar = itemView.findViewById(R.id.imgBtnFavorito);
        }

        public void bind(String item) {
            textView.setText(item);
        }
    }

    private void eliminarRestauranteFirestore(String restaurante, String usuario) {
        // Obtén una referencia al documento del usuario en Firebase Firestore
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference userRef = db.collection("usuario").document(buscarUsuarioPorNombre(usuario));

        // Elimina el restaurante de la lista "favoritos" en Firebase Firestore
        userRef.update("favoritos", FieldValue.arrayRemove(restaurante))
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d("Favoritos", "Restaurante eliminado de Firestore");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e("Favoritos", "Error al eliminar el restaurante de Firestore: " + e.getMessage());
                    }
                });
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