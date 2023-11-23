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
    private var searchClicked = false

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

        //돋보기 버튼 클릭 시 검색 바 뜨도록 하고, 다시 누르면 사라지게 설정
        val chatListSearchImageView = binding.chatListSearchImageView
        val chatListSearchFrameLayout = binding.chatListSearchFrameLayout
        chatListSearchFrameLayout.visibility = View.GONE

        chatListSearchImageView.setOnClickListener {
            searchClicked = !searchClicked
            if(searchClicked){
                chatListSearchFrameLayout.visibility = View.VISIBLE
            }
            else{
                chatListSearchFrameLayout.visibility = View.GONE
            }
        }

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}