package edu.skku.cs.chatapp.ui.friendlist

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintSet
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import edu.skku.cs.chatapp.databinding.FragmentFriendlistBinding
import edu.skku.cs.chatapp.dto.FriendListAdapter
import edu.skku.cs.chatapp.ui.SharedViewModel
import kotlinx.coroutines.*

class FriendListFragment : Fragment() {
    private lateinit var sharedViewModel: SharedViewModel
    private var _binding: FragmentFriendlistBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
    private val getFriendListScope = CoroutineScope(Dispatchers.Main)
    private var searchClicked = false
    private var id: String = ""

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFriendlistBinding.inflate(inflater, container, false)
        val root: View = binding.root

        sharedViewModel = ViewModelProvider(requireActivity()).get(SharedViewModel::class.java)
        sharedViewModel.getUserId().observe(viewLifecycleOwner) { userId ->
            id = userId
        }
        sharedViewModel.getFriendList().observe(viewLifecycleOwner) { list ->
            getFriendListScope.launch {
                val listAdapter = FriendListAdapter(requireContext(), savedInstanceState, list, id)
                val listView = binding.friendListItemView
                listView.adapter = listAdapter
            }
        }

        //돋보기 버튼 클릭 시 검색 바 뜨도록 하고, 다시 누르면 사라지게 설정
        val friendListConstraintLayout = binding.friendListConstraintLayout
        val friendListSearchImageView = binding.friendListSearchImageView
        val friendListSearchFrameLayout = binding.friendListSearchFrameLayout
        val constraintSet = ConstraintSet()
        constraintSet.clone(binding.root)
        friendListSearchFrameLayout.visibility = View.GONE

        friendListSearchImageView.setOnClickListener {
            searchClicked = !searchClicked
            if(searchClicked){
                constraintSet.connect(
                    friendListConstraintLayout.id,
                    ConstraintSet.TOP,
                    binding.guideline20Friendlist.id,
                    ConstraintSet.TOP
                )
                constraintSet.applyTo(binding.root)
                friendListSearchFrameLayout.visibility = View.VISIBLE
            }
            else{
                constraintSet.connect(
                    friendListConstraintLayout.id,
                    ConstraintSet.TOP,
                    binding.guideline10Friendlist.id,
                    ConstraintSet.TOP
                )
                constraintSet.applyTo(binding.root)
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