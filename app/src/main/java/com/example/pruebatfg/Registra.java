package com.example.pruebatfg;

import androidx.appcompat.app.AppCompatActivity;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.os.Bundle;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.firebase.FirebaseApp;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class Registra extends AppCompatActivity {

    ImageButton imgBack;
    EditText edNombre, edNombreUsuario, edClave, edRepiteClave;
    Button btnRegistrar;
    private FirebaseFirestore mfirestore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registra);

        FirebaseApp.initializeApp(getApplicationContext());

        mfirestore = FirebaseFirestore.getInstance();

        imgBack = findViewById(R.id.imgBtnBackLogo);
        edNombre = findViewById(R.id.ediNombreR);
        edNombreUsuario = findViewById(R.id.ediUsuarioR);
        edClave = findViewById(R.id.ediClaveR);
        edRepiteClave = findViewById(R.id.ediRepiteClaveR);
        btnRegistrar = findViewById(R.id.btnRegistrarR);

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

        btnRegistrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String nombre = edNombre.getText().toString();
                String nombreUsuario = edNombreUsuario.getText().toString();
                String clave = edClave.getText().toString();
                String repiteClave = edRepiteClave.getText().toString();

                if (nombre.isEmpty() || nombreUsuario.isEmpty() || clave.isEmpty() || repiteClave.isEmpty()){
                    Toast.makeText(Registra.this, "Uno o varios campos estan vacios", Toast.LENGTH_SHORT).show();
                }else{
                    comprobarClave(clave, repiteClave, nombre, nombreUsuario);
                }
            }
        });
    }

    private void comprobarClave(String clave, String repiteClave, String nombre, String nombreUsuario) {
        //aqui podemos añadir que la clave tiene que tener mas de x digitos y tiene que tener mayus minus numeros y caracteres.
        if (clave.equals(repiteClave)){
            //sigue
            Toast.makeText(this, "Las contraseñas son correctas", Toast.LENGTH_SHORT).show();
            cargaBaseDatos(nombre, nombreUsuario, clave);
        }else{
            //no sigue
            Toast.makeText(this, "Las contraseñas no son iguales", Toast.LENGTH_SHORT).show();
        }
    }

    private void cargaBaseDatos(String nombre, String nombreUsuario, String clave) {
        Map<String, Object> map = new HashMap<>();
        map.put("nombre", nombre);
        map.put("usuario", nombreUsuario);
        map.put("clave", clave);

        mfirestore.collection("usuario").add(map).addOnSuccessListener(documentReference -> {
            Toast.makeText(getApplicationContext(), "Registado Correctamente", Toast.LENGTH_SHORT).show();
            finish();
        }).addOnFailureListener(e -> Toast.makeText(getApplicationContext(), "Error al Registrar", Toast.LENGTH_SHORT).show());
    }

    //Este metodo carga la base de datos con los restaurantes.
    private void cargaBaseDatosRestaurantes(String nombre, String nombreUsuario, String clave) {
        String[] nombreRestaurante = {"La Almazara", "Los Caballos", "El Jardín de la Yedra", "El Olivar de Santa Teresa", "La Terraza del Tajo", "El Patio de Alcántara", "Restaurante Roma", "El Rinconcito", "Casa Miguel", "La Parra", "El Fogón de Gredos", "El Rincón de los Sabores", "La Tapería", "Restaurante Italia", "El Rincón del Duque", "El Paladar", "La Parrilla", "Restaurante del Parador", "El Rincón de María", "La Taberna", "El Rincón del Mar", "La Piazzetta", "El Fogón de los Sabores", "La Terraza de Mérida", "El Rincón del Cordero", "Casa Manolo", "El Laurel", "Casa Pedro", "El Patio de Cáceres", "La Ponderosa", "El Almirez", "Restaurante Juanito", "La Bodeguilla", "Los Arcos", "La Marmita", "Casa Teo", "El Puchero", "Restaurante El Conquistador", "El Fogón de Trujillo", "Casa Manuela", "El Mirador de Guadalupe", "La Tasca del Abuelo", "El Padrino", "Restaurante El Lago", "La Hospedería", "El Bodegón", "Casa Pepe", "El Mirador", "La Casona", "El Fogón de la Abuela", "La Vinoteca", "Casa Paco", "El Rincón del Pintor", "La Casita del Bosque", "El Rinconcito de la Plaza", "Casa Manolita", "El Asador", "La Tablita", "El Balcón del Guadiana", "La Fonda", "El Trujillano", "La Bodega de Juan", "El Albergue", "La Cacharrería", "El Mesón de Plasencia", "La Terraza de Cáceres", "El Rinconcito de los Sabores", "El Rinconcito del Duque", "La Cabaña", "El Fogón de la Montaña"};
        String[] direccion = {"Calle Cánovas del Castillo", "Carretera de Badajoz, km 8", "Calle Coria, 20", "Calle San Antonio, 2", "Calle Ribera del Tajo, 6", "Calle Alcántara, 10", "Calle Roma, 15", "Calle Mayor, 8", "Avenida de Extremadura, 20", "Calle Real, 5", "Calle Gredos, 2", "Calle Sabores, 12", "Calle San Pedro, 8", "Avenida de Italia, 5", "Calle Duque de San Carlos, 10", "Calle Paladar, 15", "Avenida de la Parrilla, 20", "Plaza de Cáceres, 1", "Calle María, 8", "Calle del Sol, 10", "Avenida del Mar, 20", "Plaza Italia, 5", "Calle Sabores, 10", "Calle Almendralejo, 12", "Avenida de los Pastores, 5", "Plaza Mayor, 10", "Calle Santa Eulalia, 8", "Calle San Juan, 15", "Calle Amargura, 20", "Carretera de Trujillo, km 6", "Calle Alzapiernas, 30", "Calle Calvario, 8", "Plaza de San Andrés, 12", "Avenida de Extremadura, 42", "Calle Virgen de la Montaña", "Calle San Francisco, 15", "Calle Estrella, 7", "Calle Hernán Cortés, 3", "Plaza Mayor, 8", "Calle San Roque, 12", "Calle Virgen de Guadalupe", "Calle Santa María, 25", "Avenida de la Constitución", "Calle del Embalse, 10", "Calle San Juan, 5", "Plaza Mayor, 8", "Avenida de la Constitución, 15", "Calle Serrano, 10", "Carretera de Trujillo, km 4", "Calle Mayor, 12", "Calle San Pedro, 20", "Avenida de Extremadura, 40", "Calle Pintor, 8", "Carretera del Bosque, km 2", "Plaza de España, 5", "Calle Real, 15", "Calle de los Asadores, 2", "Calle Mayor, 10"};
        String[] poblacion = {"Mérida", "Cáceres", "Plasencia", "Badajoz", "Cáceres", "Mérida", "Plasencia", "Badajoz", "Mérida", "Cáceres", "Plasencia", "Badajoz", "Mérida", "Cáceres", "Plasencia", "Badajoz", "Mérida", "Cáceres", "Plasencia", "Badajoz", "Mérida", "Cáceres", "Plasencia", "Mérida", "Cáceres", "Plasencia", "Badajoz", "Mérida", "Cáceres", "Trujillo", "Mérida", "Cáceres", "Badajoz", "Mérida", "Cáceres", "Plasencia", "Badajoz", "Mérida", "Trujillo", "Cáceres", "Guadalupe", "Badajoz", "Mérida", "Cáceres", "Mérida", "Cáceres", "Badajoz", "Plasencia", "Mérida", "Cáceres", "Badajoz", "Mérida", "Cáceres", "Plasencia", "Badajoz", "Mérida", "Cáceres", "Badajoz", "Mérida", "Cáceres", "Plasencia", "Badajoz", "Mérida", "Cáceres", "Badajoz", "Cáceres", "Plasencia", "Badajoz", "Mérida", "Cáceres"};
        String[] precio = {"€€", "€€€", "€€", "€€€", "€€", "€€", "€€€", "€", "€€", "€€€", "€€", "€€€", "€", "€€", "€€€", "€€", "€€", "€€€", "€", "€€", "€€", "€€€", "€€", "€€", "€€", "€€€", "€", "€€", "€€€", "€", "€€", "€€€", "€", "€€€", "€€", "€", "€€", "€€€", "€€", "€€€", "€€€", "€", "€€", "€€€", "€€", "€€", "€€", "€€€", "€€€", "€€", "€€", "€€", "€€€", "€€", "€", "€€€", "€€", "€€", "€€€", "€", "€€", "€", "€€€", "€€€", "€€", "€€€", "€", "€€", "€"};
        String[] tipoComida = {"Mediterránea", "Carnes", "Cocina de autor", "Mediterránea", "Mediterránea", "Tradicional", "Italiana", "Tapas", "Cocina casera", "Cocina de autor", "Cocina tradicional", "Fusion", "Tapas", "Italiana", "Carnes", "Cocina creativa", "Parrilla", "Cocina de autor", "Tradicional", "Tapas", "Mariscos", "Italiana", "Cocina trad", "Mediterránea", "Carnes", "Alta cocina", "Tapas", "Tradicional", "Fusión", "Parrilla", "Mediterránea", "Gastronomía local", "Tapas", "Cocina de autor", "Mediterránea", "Tradicional", "Cocina casera", "Mediterránea", "Cocina regional", "Alta cocina", "Cocina creativa", "Tapas", "Italiana", "Mediterránea", "Cocina tradicional", "Tapas", "Cocina casera", "Cocina de autor", "Mediterránea", "Cocina tradicional", "Tapas", "Cocina casera", "Cocina creativa", "Cocina de autor", "Tapas", "Alta cocina", "Carnes", "Cocina española", "Mediterránea", "Cocina de autor", "Cocina tradicional", "Tapas", "Cocina regional", "Fusión", "Alta cocina", "Mediterránea", "Fusión", "Tapas", "Cocina tradicional", "Tapas"};

        Map<String, Object> map = new HashMap<>();
        for (int i = 0; i < nombreRestaurante.length; i++) {
            map.put("nombre", nombreRestaurante[i]);
            map.put("direccion", direccion[i]);
            map.put("poblacion", poblacion[i]);
            map.put("precio", precio[i]);
            map.put("tipocomida", tipoComida[i]);

            mfirestore.collection("restaurante").add(map).addOnSuccessListener(documentReference -> {
                Toast.makeText(getApplicationContext(), "Registado Correctamente", Toast.LENGTH_SHORT).show();
                finish();
            }).addOnFailureListener(e -> Toast.makeText(getApplicationContext(), "Error al Registrar", Toast.LENGTH_SHORT).show());
        }
    }
}