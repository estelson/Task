package com.exemplo.task.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.exemplo.task.R
import com.exemplo.task.databinding.FragmentTaskFormBinding
import com.exemplo.task.helper.FirebaseHelper
import com.exemplo.task.model.Task

class TaskFormFragment: Fragment() {

    private val args: TaskFormFragmentArgs by navArgs()

    private var _binding: FragmentTaskFormBinding? = null
    private val binding get() = _binding!!

    private lateinit var task: Task
    private var newTask: Boolean = true

    private var taskStatus: Int = 0

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? { // Inflate the layout for this fragment
        _binding = FragmentTaskFormBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initListeners()

        getArgs()
    }

    private fun getArgs() {
        args.task?.let { tsk: Task ->
            if(tsk != null) {
                task = tsk

                configTask()
            }
        }
    }

    private fun configTask() {
        newTask = false

        taskStatus = task.status
        binding.textToolbar.text = getString(R.string.text_edit_task_title)

        binding.edtTaskDescription.setText(task.taskDescription)

        setStatus()
    }

    private fun setStatus() {
        binding.radioGroup.check(
            when(task.status) {
                0 -> {
                    R.id.rbTodo
                }

                1 -> {
                    R.id.rbDoing
                }

                else -> {
                    R.id.rbDone
                }
            }
        )
    }

    private fun initListeners() {
        binding.btnSave.setOnClickListener {
            validateData()
        }

        binding.radioGroup.setOnCheckedChangeListener { _, id ->
            taskStatus = when(id) {
                R.id.rbTodo -> 0
                R.id.rbDoing -> 1
                else -> 2
            }
        }
    }

    private fun validateData() {
        val taskDescription: String = binding.edtTaskDescription.text.toString().trim()
        if(taskDescription.isNotEmpty()) {
            binding.progressBar.isVisible = true

            if(newTask) {
                task = Task()
            }

            task.taskDescription = taskDescription
            task.status = taskStatus

            saveTask()
        } else {
            binding.edtTaskDescription.requestFocus()
            binding.edtTaskDescription.error = getString(R.string.error_empty_text_description)
        }
    }

    private fun saveTask() {
        FirebaseHelper.getDatabase()
            .child("task")
            .child(FirebaseHelper.getIdUser() ?: "")
            .child(task.id)
            .setValue(task)
            .addOnCompleteListener { task ->
                if(task.isSuccessful) {
                    if(newTask) {
                        findNavController().popBackStack()

                        Toast.makeText(requireContext(), getString(R.string.message_on_complete_add_task), Toast.LENGTH_LONG).show()
                    } else {
                        binding.progressBar.isVisible = false

                        Toast.makeText(requireContext(), getString(R.string.message_on_complete_edit_task), Toast.LENGTH_LONG).show()
                    }
                } else {
                    if(newTask) {
                        Toast.makeText(requireContext(), getString(R.string.error_on_failure_add_task, task.exception?.message.toString()), Toast.LENGTH_LONG).show()
                    } else {
                        Toast.makeText(requireContext(), getString(R.string.error_on_failure_edit_task, task.exception?.message.toString()), Toast.LENGTH_LONG).show()
                    }
                }
            }.addOnFailureListener { error ->
                binding.progressBar.isVisible = false

                if(newTask) {
                    Toast.makeText(requireContext(), getString(R.string.error_on_failure_add_task, error.message.toString()), Toast.LENGTH_LONG).show()
                } else {
                    Toast.makeText(requireContext(), getString(R.string.error_on_failure_edit_task, error.message.toString()), Toast.LENGTH_LONG).show()
                }
            }
    }

    override fun onDestroyView() {
        super.onDestroyView()

        _binding = null
    }

}