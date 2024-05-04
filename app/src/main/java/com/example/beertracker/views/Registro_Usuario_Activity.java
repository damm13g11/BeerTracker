package com.example.beertracker.views;


import android.app.DatePickerDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import org.threeten.bp.LocalDate;

import com.example.beertracker.R;
import com.example.beertracker.models.Usuario;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.Calendar;

public class Registro_Usuario_Activity extends AppCompatActivity {

    Button btnRegister;
    Button btnFoto;
    EditText editTextEmailRegister;
    EditText editTextPasswordRegister;
    EditText editTextName;
    EditText editTextLastName;
    RadioButton radioButtonMale;
    RadioButton radioButtonFemale;
    RadioGroup radioGroupGender;
    Button buttonFechaNacimiento;
    private int generoSeleccionado;
    private LocalDate fechaSeleccionada;
    private static final int PICK_IMAGE_REQUEST = 1;
    private Uri imageUri;
    FirebaseFirestore db = FirebaseFirestore.getInstance();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registro_usuario);

        btnRegister = findViewById(R.id.btnRegister);
        btnFoto = findViewById(R.id.btnFoto);
        editTextEmailRegister = findViewById(R.id.editTextEmailRegister);
        editTextPasswordRegister = findViewById(R.id.editTextPasswordRegister);
        editTextName = findViewById(R.id.editTextName);
        editTextLastName = findViewById(R.id.editTextLastName);
        buttonFechaNacimiento = findViewById(R.id.buttonFechaNacimiento);
        radioButtonMale = findViewById(R.id.radioButtonMale);
        radioButtonFemale = findViewById(R.id.radioButtonFemale);
        radioGroupGender = findViewById(R.id.radioGroupGender);

        androidx.appcompat.widget.Toolbar toolbar = findViewById(R.id.toolbar);

        buttonFechaNacimiento.setOnClickListener(view -> mostrarCalendario());

        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                registrarUsuario();
            }
        });

        btnFoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                seleccionarFoto();
            }
        });

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Acción para volver a la actividad de inicio de sesión
                onBackPressed();
            }
        });
    }

    private void mostrarCalendario() {
        // Obtener la fecha actual
        final Calendar calendario = Calendar.getInstance();
        int año = calendario.get(Calendar.YEAR);
        int mes = calendario.get(Calendar.MONTH);
        int día = calendario.get(Calendar.DAY_OF_MONTH);

        // Crear un diálogo de selección de fecha
        DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                (view, selectedYear, selectedMonth, selectedDay) -> {
                    // Construir un objeto LocalDate
                    fechaSeleccionada = LocalDate.of(selectedYear, selectedMonth + 1, selectedDay);
                    Toast.makeText(Registro_Usuario_Activity.this, "Fecha seleccionada: " + fechaSeleccionada.toString(), Toast.LENGTH_SHORT).show();
                },
                año, mes, día);

        // Mostrar el diálogo de selección de fecha
        datePickerDialog.show();
    }

    private void seleccionarFoto() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Selecciona una foto"), PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            imageUri = data.getData();
        }
    }

    public void registrarUsuario() {
        String email = editTextEmailRegister.getText().toString().trim();
        String password = editTextPasswordRegister.getText().toString().trim();
        String nombre = editTextName.getText().toString();
        String apellidos = editTextLastName.getText().toString();
        generoSeleccionado = seleccionarGenero();

        if (validarMail(email) && !password.isEmpty()) {
            FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, password).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    Usuario usuario = new Usuario(email, nombre, apellidos, generoSeleccionado, fechaSeleccionada);

                    if (imageUri != null) {
                        StorageReference storageReference = FirebaseStorage.getInstance().getReference().child("images/profile_images/" + email + ".jpg");
                        storageReference.putFile(imageUri).addOnSuccessListener(taskSnapshot -> {
                            storageReference.getDownloadUrl().addOnSuccessListener(uri -> {
                                usuario.setFotoUri(uri.toString());
                                db.collection("users").document(email).set(usuario);
                                Intent intent = new Intent(Registro_Usuario_Activity.this, Login_Activity.class);
                                intent.putExtra("email", email);
                                startActivity(intent);
                                finish();
                            }).addOnFailureListener(exception -> {
                                Toast.makeText(Registro_Usuario_Activity.this, "Error al obtener URL de la imagen", Toast.LENGTH_SHORT).show();
                            });
                        }).addOnFailureListener(exception -> {
                            Toast.makeText(Registro_Usuario_Activity.this, "Error al subir imagen", Toast.LENGTH_SHORT).show();
                        });
                    } else {
                        db.collection("users").document(email).set(usuario);
                        Intent intent = new Intent(Registro_Usuario_Activity.this, Login_Activity.class);
                        intent.putExtra("email", email);
                        startActivity(intent);
                        finish();
                    }
                } else {
                    Toast.makeText(Registro_Usuario_Activity.this, "Error al registrar usuario", Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            Toast.makeText(Registro_Usuario_Activity.this, "Correo electrónico o contraseña inválidos", Toast.LENGTH_SHORT).show();
        }
    }

    public boolean validarMail(String email){
        String patron = "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";
        return email.matches(patron);
    }

    public int seleccionarGenero(){
        int checkedId = radioGroupGender.getCheckedRadioButtonId();
            if (checkedId == R.id.radioButtonMale) {
                generoSeleccionado = 1;
            } else if (checkedId == R.id.radioButtonFemale){
                generoSeleccionado = 2;
            } else {
                generoSeleccionado = 0;
            };
        return generoSeleccionado;
    }
}
