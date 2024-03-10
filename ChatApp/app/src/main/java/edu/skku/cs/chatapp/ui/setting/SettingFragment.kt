package edu.skku.cs.chatapp.ui.setting

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import edu.skku.cs.chatapp.*
import edu.skku.cs.chatapp.databinding.FragmentSettingBinding

class SettingFragment : Fragment() {

    private var _binding: FragmentSettingBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentSettingBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val profileButton = binding.profileSettingButton
        val chatButton = binding.chatSettingButton
        val logoutButton = binding.logoutButton
        profileButton.setOnClickListener {
            val intent = Intent(requireContext(), ProfileSetActivity::class.java)
            startActivity(intent, savedInstanceState)
        }

        chatButton.setOnClickListener {
            val intent = Intent(requireContext(), ChatSetActivity::class.java)
            startActivity(intent)
        }

        logoutButton.setOnClickListener {
            requireActivity().finish()
            val intent = Intent(requireContext(), StartActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
        }

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}