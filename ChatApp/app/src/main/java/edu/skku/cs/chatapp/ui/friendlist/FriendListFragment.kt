package edu.skku.cs.chatapp.ui.friendlist

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import edu.skku.cs.chatapp.databinding.FragmentFriendlistBinding

class FriendListFragment : Fragment() {

    private var _binding: FragmentFriendlistBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
    private var searchClicked = false
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val friendListViewModel =
            ViewModelProvider(this).get(FriendListViewModel::class.java)

        _binding = FragmentFriendlistBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val textView: TextView = binding.textFriendlist
        friendListViewModel.text.observe(viewLifecycleOwner) {
            textView.text = it
        }

        //돋보기 버튼 클릭 시 검색 바 뜨도록 하고, 다시 누르면 사라지게 설정
        val friendListSearchImageView = binding.friendListSearchImageView
        val friendListSearchFrameLayout = binding.friendListSearchFrameLayout
        friendListSearchFrameLayout.visibility = View.GONE

        friendListSearchImageView.setOnClickListener {
            searchClicked = !searchClicked
            if(searchClicked){
                friendListSearchFrameLayout.visibility = View.VISIBLE
            }
            else{
                friendListSearchFrameLayout.visibility = View.GONE
            }
        }

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}