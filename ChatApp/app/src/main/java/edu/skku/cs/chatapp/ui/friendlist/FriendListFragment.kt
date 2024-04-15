package edu.skku.cs.chatapp.ui.friendlist

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintSet
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.google.gson.Gson
import edu.skku.cs.chatapp.Utils
import edu.skku.cs.chatapp.databinding.FragmentFriendlistBinding
import edu.skku.cs.chatapp.dto.Friend
import edu.skku.cs.chatapp.dto.FriendListAdapter
import edu.skku.cs.chatapp.dto.FriendListResponse
import edu.skku.cs.chatapp.dto.UserFindListResponse
import edu.skku.cs.chatapp.ui.SharedViewModel
import kotlinx.coroutines.*
import okhttp3.*
import java.io.IOException

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
        val friendListSearchButton = binding.friendListSearchButton
        val friendListSearchEditText = binding.friendListSearchEditText
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
                sharedViewModel.setIfSearching(1)
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
                sharedViewModel.setIfSearching(0)
            }
        }

        friendListSearchButton.setOnClickListener {
            val searchName = friendListSearchEditText.text.toString()
            if(searchName.length == 0){
                Toast.makeText(requireContext(), "이름을 입력해주세요", Toast.LENGTH_SHORT).show()
            }
            else{
                val client = OkHttpClient()
                val host = Utils.SERVER_URL

                val path = "/userfind/" + id + "/" + searchName
                val req = Request.Builder().url(host+path).get().build()

                client.newCall(req).enqueue(object : Callback {
                    override fun onFailure(call: Call, e: IOException) {
                        e.printStackTrace()
                    }

                    override fun onResponse(call: Call, response: Response) {
                        response.use {
                            if(!response.isSuccessful)throw IOException("Unexpected code $response")
                            val str = response.body!!.string()
                            Log.d("response", str)
                            val data = Gson().fromJson(str, UserFindListResponse::class.java)
                            CoroutineScope(Dispatchers.Main).launch {
                                if(data.Status == "success"){
                                    val friendList = mutableListOf<Friend>()
                                    val friendsArray = data.Users
                                    for (friendData in friendsArray) {
                                        // Assuming 'image' is a byte array
                                        //val bitmap = BitmapFactory.decodeByteArray(friendData.image, 0, friendData.image.size)
                                        val friend = Friend(friendData.ChatId, friendData.Id, friendData.UserName, friendData.Email, friendData.Image, friendData.Friend)
                                        friendList.add(friend)
                                    }

                                    val listAdapter = FriendListAdapter(requireContext(), savedInstanceState, friendList, id)
                                    val listView = binding.friendListItemView
                                    listView.adapter = listAdapter
                                }
                                else{
                                    Toast.makeText(requireContext(), "해당하는 이름의 유저가 없습니다.", Toast.LENGTH_SHORT).show()
                                }
                            }
                        }
                    }
                })
            }
        }

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}