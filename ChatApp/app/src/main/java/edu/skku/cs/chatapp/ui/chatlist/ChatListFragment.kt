package edu.skku.cs.chatapp.ui.chatlist

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import edu.skku.cs.chatapp.databinding.FragmentChatlistBinding

class ChatListFragment : Fragment() {

    private var _binding: FragmentChatlistBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val chatListViewModel =
            ViewModelProvider(this).get(ChatListViewModel::class.java)

        _binding = FragmentChatlistBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val textView: TextView = binding.textChatlist
        chatListViewModel.text.observe(viewLifecycleOwner) {
            textView.text = it
        }
        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}