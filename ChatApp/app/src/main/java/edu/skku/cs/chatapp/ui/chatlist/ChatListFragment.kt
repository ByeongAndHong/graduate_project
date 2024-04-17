package edu.skku.cs.chatapp.ui.chatlist

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import edu.skku.cs.chatapp.databinding.FragmentChatlistBinding
import edu.skku.cs.chatapp.dto.ChatListAdapter
import edu.skku.cs.chatapp.ui.SharedViewModel
import kotlinx.coroutines.*

class ChatListFragment : Fragment() {
    private lateinit var sharedViewModel: SharedViewModel
    private var _binding: FragmentChatlistBinding? = null
    private val updateChatListScope = CoroutineScope(Dispatchers.Main)
    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
    private var id: String = ""

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentChatlistBinding.inflate(inflater, container, false)
        val root: View = binding.root

        sharedViewModel = ViewModelProvider(requireActivity()).get(SharedViewModel::class.java)
        sharedViewModel.getUserId().observe(viewLifecycleOwner) { userId ->
            id = userId
        }
        sharedViewModel.getChatList().observe(viewLifecycleOwner) { list ->
            updateChatListScope.launch {
                while(true) {
                    val listAdapter = ChatListAdapter(requireContext(), savedInstanceState, list, id)
                    val listView = binding.chatListItemView
                    listView.adapter = listAdapter

                    delay(500)
                }
            }
        }

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        updateChatListScope.cancel()
        _binding = null
    }
}