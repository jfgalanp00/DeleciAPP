package com.example.pruebatfg.adapter;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pruebatfg.R;
import com.example.pruebatfg.modelo.Restaurante;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
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

public class RestauranteAdapter extends FirestoreRecyclerAdapter<Restaurante, RestauranteAdapter.ViewHolder> {
    private String nombreUsuario;
    /**
     * Create a new RecyclerView adapter that listens to a Firestore Query.  See {@link
     * FirestoreRecyclerOptions} for configuration options.
     *
     * @param options
     */

    public RestauranteAdapter(@NonNull FirestoreRecyclerOptions<Restaurante> options, String usuario) {
        super(options);
        this.nombreUsuario = usuario;
        Log.e("Documento", usuario);
    }


    @Override
    protected void onBindViewHolder(@NonNull ViewHolder holder, int position, @NonNull Restaurante model) {
        //mostar los datos
        holder.direccion.setText(model.getDireccion());
        holder.nombre.setText(model.getNombre());
        holder.poblacion.setText(model.getPoblacion());
        holder.precio.setText(model.getPrecio());
        holder.tipocomida.setText(model.getTipocomida());

        // Actualizar visualmente el estado de la estrella según el estado de favorito del restaurante
        // Obtener el ID del usuario actual ingresado por el usuario, hay que sacar la lista de todos los documentos de la base de datos.
        // String usuarioId = "pm6glCqPEBSgsDLsjgH6"; // Reemplaza esto con el ID del usuario actual ingresado por el usuario

        // aqui le estamos pasando un string porque no es posible pasarle una lista, como hacemos para que vaya iterando la lista y sea un string en vez de lista
        // Obtener la referencia del documento del usuario actual

        DocumentReference usuarioRef = FirebaseFirestore.getInstance().collection("usuario").document(buscarUsuarioPorNombre(nombreUsuario));

        usuarioRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if (documentSnapshot.exists()) {
                    List<String> favoritos = (List<String>) documentSnapshot.get("favoritos");
                    if (favoritos != null && favoritos.contains(model.getNombre())) {
                        model.setFavorito(true);
                        holder.imgButonFavorito.setImageResource(R.drawable.favoritolleno);
                    } else {
                        model.setFavorito(false);
                        holder.imgButonFavorito.setImageResource(R.drawable.favoritovacio);
                    }
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                // Ocurrió un error al obtener la lista de favoritos del usuario
            }
        });

        // Configurar el clic listener de la estrella
        final int itemPosition = position; // Variable final
        holder.imgButonFavorito.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Crear animación de rotación
                ObjectAnimator rotationAnimator = ObjectAnimator.ofFloat(v, "rotation", 0f, 360f);
                rotationAnimator.setDuration(1000);
                rotationAnimator.setInterpolator(new AccelerateDecelerateInterpolator());

                // Crear animación de escala
                ObjectAnimator scaleAnimatorX = ObjectAnimator.ofFloat(v, "scaleX", 1f, 1.2f, 1f);
                scaleAnimatorX.setDuration(500);
                scaleAnimatorX.setInterpolator(new AccelerateDecelerateInterpolator());

                ObjectAnimator scaleAnimatorY = ObjectAnimator.ofFloat(v, "scaleY", 1f, 1.2f, 1f);
                scaleAnimatorY.setDuration(500);
                scaleAnimatorY.setInterpolator(new AccelerateDecelerateInterpolator());

                // Combinar las animaciones en un AnimatorSet
                AnimatorSet animatorSet = new AnimatorSet();
                animatorSet.playSequentially(rotationAnimator, scaleAnimatorX, scaleAnimatorY);
                animatorSet.start();

                // Cambiar la imagen después de que termine la animación
                animatorSet.addListener(new Animator.AnimatorListener() {
                    @Override
                    public void onAnimationStart(Animator animation) {
                        // Acción a realizar al comenzar la animación (opcional)
                    }

                    @Override
                    public void onAnimationEnd(Animator animation) {
                        // Acción a realizar al finalizar la animación
                        // Cambiar la imagen del botón
                        if (model.isFavorito()) {
                            holder.imgButonFavorito.setImageResource(R.drawable.favoritolleno);
                        } else {
                            holder.imgButonFavorito.setImageResource(R.drawable.favoritovacio);
                        }
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

                // Cambiar el estado de favorito del restaurante
                boolean isFavorito = !model.isFavorito();
                model.setFavorito(isFavorito);

                // Actualizar el campo "favoritos" del documento del usuario
                if (isFavorito) {
                    // Agregar el restaurante a los favoritos del usuario
                    usuarioRef.update("favoritos", FieldValue.arrayUnion(model.getNombre()))
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void unused) {
                                    // El restaurante se agregó a los favoritos correctamente
                                    model.setFavorito(true);
                                    notifyItemChanged(itemPosition);
                                    //holder.imgButonFavorito.setImageResource(R.drawable.favoritovacio);
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    // Ocurrió un error al agregar el restaurante a los favoritos
                                }
                            });
                } else {
                    // Eliminar el restaurante de los favoritos del usuario
                    usuarioRef.update("favoritos", FieldValue.arrayRemove(model.getNombre()))
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    // El restaurante se eliminó de los favoritos correctamente
                                    model.setFavorito(false);
                                    notifyItemChanged(itemPosition);
                                    //holder.imgButonFavorito.setImageResource(R.drawable.favoritolleno);
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    // Ocurrió un error al eliminar el restaurante de los favoritos
                                }
                            });
                }
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

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        //aqui se conecta  el adaptador con el layout creado
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.view_restaurante_single, parent, false);
        return new ViewHolder(v);
    }

    //referenciar datos
    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView direccion, nombre, poblacion, precio, tipocomida;
        ImageButton imgButonFavorito;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            direccion = itemView.findViewById(R.id.txtDireccionL);
            nombre = itemView.findViewById(R.id.txtNombreFav);
            poblacion = itemView.findViewById(R.id.txtPoblacionL);
            precio = itemView.findViewById(R.id.txtPrecioL);
            tipocomida = itemView.findViewById(R.id.txtTipocomidaL);
            imgButonFavorito = itemView.findViewById(R.id.imgBtnFavorito);
        }
    }
}