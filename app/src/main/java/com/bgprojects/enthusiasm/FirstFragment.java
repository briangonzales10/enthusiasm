package com.bgprojects.enthusiasm;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import com.bgprojects.enthusiasm.databinding.FragmentFirstBinding;

public class FirstFragment extends Fragment {

    private FragmentFirstBinding binding;

    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {

        binding = FragmentFirstBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding.randomButton.setOnClickListener(view2 -> {
            int currentCount = Integer.parseInt(binding.textviewFirst.getText().toString());
            FirstFragmentDirections.ActionFirstFragmentToSecondFragment action =
                    FirstFragmentDirections.actionFirstFragmentToSecondFragment(currentCount);

            NavHostFragment.findNavController(FirstFragment.this).navigate(action);
        });

        binding.countButton.setOnClickListener(this::countMe);
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

}