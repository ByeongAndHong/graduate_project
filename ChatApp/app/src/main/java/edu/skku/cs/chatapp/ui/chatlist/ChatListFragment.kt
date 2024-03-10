package edu.skku.cs.chatapp.ui.chatlist

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintSet
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import edu.skku.cs.chatapp.databinding.FragmentChatlistBinding
import edu.skku.cs.chatapp.dto.ChatListAdapter
import edu.skku.cs.chatapp.dto.FriendListAdapter
import edu.skku.cs.chatapp.ui.SharedViewModel
import kotlinx.coroutines.*

class ChatListFragment : Fragment() {
    private lateinit var sharedViewModel: SharedViewModel
    private var _binding: FragmentChatlistBinding? = null
    private val updateChatListScope = CoroutineScope(Dispatchers.Main)
    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
    private var searchClicked = false
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

        //돋보기 버튼 클릭 시 검색 바 뜨도록 하고, 다시 누르면 사라지게 설정
        val chatListConstraintLayout = binding.chatListConstraintLayout
        val chatListSearchImageView = binding.chatListSearchImageView
        val chatListSearchFrameLayout = binding.chatListSearchFrameLayout
        val constraintSet = ConstraintSet()
        constraintSet.clone(binding.root)
        chatListSearchFrameLayout.visibility = View.GONE

        chatListSearchImageView.setOnClickListener {
            searchClicked = !searchClicked
            if(searchClicked){
                constraintSet.connect(
                    chatListConstraintLayout.id,
                    ConstraintSet.TOP,
                    binding.guideline20Chatlist.id,
                    ConstraintSet.TOP
                )
                constraintSet.applyTo(binding.root)
                chatListSearchFrameLayout.visibility = View.VISIBLE
            }
            else{
                constraintSet.connect(
                    chatListConstraintLayout.id,
                    ConstraintSet.TOP,
                    binding.guideline10Chatlist.id,
                    ConstraintSet.TOP
                )
                constraintSet.applyTo(binding.root)
                chatListSearchFrameLayout.visibility = View.GONE
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