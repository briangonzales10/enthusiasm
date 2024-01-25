package com.bgprojects.enthusiasm;

import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import org.vosk.Model;
import org.vosk.Recognizer;
import org.vosk.android.RecognitionListener;
import org.vosk.android.SpeechService;
import org.vosk.android.SpeechStreamService;
import org.vosk.android.StorageService;


import com.bgprojects.enthusiasm.databinding.FragmentFirstBinding;

import java.io.IOException;

public class FirstFragment extends Fragment implements RecognitionListener {

    private FragmentFirstBinding binding;
    private static final int PERMISSIONS_REQUEST_RECORD_AUDIO = 1;
    private static final int STATE_START = 0;
    private static final int STATE_READY = 1;
    private static final int STATE_DONE = 2;
    private static final int STATE_FILE = 3;
    private static final int STATE_MIC = 4;
    private Model model;
    private SpeechService speechService;
    private SpeechStreamService speechStreamService;
    private TextView resultView;

    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {

        binding = FragmentFirstBinding.inflate(inflater, container, false);
        return binding.getRoot();

    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        int permissionCheck = ContextCompat.checkSelfPermission(
                binding.getRoot().getContext(), android.Manifest.permission.RECORD_AUDIO);
        if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                    this.requireActivity(),
                    new String[]{android.Manifest.permission.RECORD_AUDIO},
                    PERMISSIONS_REQUEST_RECORD_AUDIO);
        } else {
            initModel();
        }

        binding.randomButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int currentCount = Integer.parseInt(binding.textviewFirst.getText().toString());
                FirstFragmentDirections.ActionFirstFragmentToSecondFragment action =
                        FirstFragmentDirections.actionFirstFragmentToSecondFragment(currentCount);

                NavHostFragment.findNavController(FirstFragment.this).navigate(action);
            }
        });

        binding.recognizeMic.setOnClickListener(recView -> recognizeMicrophone());

        binding.countButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                countMe(view);
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    private void countMe(View view) {
        String countString = binding.textviewFirst.getText().toString();
        Integer count = Integer.parseInt(countString);
        count++;
        binding.textviewFirst.setText(count.toString());
    }

    private void initModel() {
        StorageService.unpack(this.getContext(), "models/model-en-us", "model",
                (model) -> {
                    this.model = model;
                    setUiState(STATE_READY);
                },
                (exception) -> setErrorState("Failed to unpack the model " + exception.getMessage()));
    }

    private void setUiState(int state) {
        binding.resultText.setText(R.string.preparing);
    }

    private void setErrorState(String message) {
        binding.resultText.setText(message);
        binding.recognizeMic.setEnabled(false);
    }

    private void recognizeMicrophone() {
            if (speechService != null) {
                speechService.stop();
                speechService = null;
            }
            else {
                try {
                    Recognizer recognizer = new Recognizer(model, 16000.0f);
                    speechService = new SpeechService(recognizer, 16000.0f);
                    speechService.startListening(this);
                } catch (IOException ioe) {
                    setErrorState(ioe.getMessage());
                }
            }
        }

    @Override
    public void onPartialResult(String hypothesis) {
        resultView.append(hypothesis + "\n");
    }

    @Override
    public void onResult(String hypothesis) {
        resultView.append(hypothesis + "\n");
    }

    @Override
    public void onFinalResult(String hypothesis) {
        resultView.append(hypothesis + "\n");
        setUiState(STATE_DONE);
        if (speechStreamService != null) {
            speechStreamService = null;
        }
    }

    @Override
    public void onError(Exception exception) {
        setErrorState(exception.getMessage());
    }

    @Override
    public void onTimeout() {
        resultView.setText(R.string.timeout);
    }

}