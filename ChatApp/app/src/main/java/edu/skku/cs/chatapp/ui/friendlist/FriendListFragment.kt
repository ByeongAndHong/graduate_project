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
        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}