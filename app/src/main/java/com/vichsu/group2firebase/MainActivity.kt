package com.vichsu.group2firebase

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.chip.ChipGroup
import com.google.firebase.Timestamp
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.vichsu.group2firebase.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    companion object {
        const val TAG = "Firebasedata"
        const val TAG_USER = "userData"
    }

    lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(LayoutInflater.from(this))
        setContentView(binding.root)

        val articleData = Firebase.firestore.collection("articles")

        // Add Data
        binding.buttonEnter.setOnClickListener {
            val newId = articleData.document().id
            val title = binding.edittextTitle.text.toString()
            val content = binding.edittextContent.text.toString()
            val article = hashMapOf(
                "id" to newId,
                "author_id" to "Vic Hsu",
                "title" to title,
                "tag" to getTag(binding.chipGroup),
                "content" to content,
                "created_time" to Timestamp.now()
            )

            articleData.document(newId)
                .set(article)
                .addOnSuccessListener { documentReference ->
                }
                .addOnFailureListener { e ->
                    Log.w(TAG, "Error adding document", e)
                }

        }

        articleData.addSnapshotListener { snapshot, e ->
            if (e != null) {
                Log.w(TAG, "Listen failed.", e)
                return@addSnapshotListener
            }

            if (snapshot != null && !snapshot.isEmpty) {
                for (document in snapshot) {
                    Log.d(TAG, "${document.id} => ${document.data}")
                }
//                snapshot.sortedBy { snapshot.query.get("") }
//                Log.d(TAG,"${snapshot.first().data}")
            } else {
                Log.d(TAG, "Current data: null")
            }
        }

        // My Account
        val userData = Firebase.firestore.collection("users")
        var currentUserEmail: String = ""
        val currentUserId = "DzH10vzpE105GK00vtu5"
        val adapter = requestAdapter(requestAdapter.OnClickListener { email, boolean ->
            if (boolean == true) {
                //加入
                userData.document(currentUserId).update("friendList", FieldValue.arrayUnion(email))
                userData.document(currentUserId).update("requestList", FieldValue.arrayRemove(email))
                userData.whereEqualTo("email", email).get().addOnSuccessListener {
                    userData.document(it.first().id)
                        .update("friendList", FieldValue.arrayUnion(currentUserEmail))
                }
            } else {
                //拒絕
                userData.document(currentUserId).update("requestList", FieldValue.arrayRemove(email))
            }
        })
        binding.recyclerRequest.adapter = adapter

        // 個人資料
        userData.document(currentUserId).get().addOnSuccessListener {
            binding.userName.text = it.get("name").toString()
            currentUserEmail = it.get("email").toString()
        }

        // 監聽request 與 friendList 資料
        userData.document(currentUserId).addSnapshotListener { value, error ->
            adapter.submitList(value?.data?.get("requestList") as ArrayList<String>)
            userData.document(currentUserId).get().addOnSuccessListener {
                binding.friendList.text = it.data!!.get("friendList").toString()
                Log.d(TAG_USER,it.data!!.get("friendList").toString())
                Log.d(TAG_USER,it.data!!.get("requestList").toString())
            }
        }

        // 透過Email找到目標ID，操作目標Document加入request資料
        var targetId = ""
        binding.buttonSend.setOnClickListener {
            val targetEmail = binding.sendRequestEmail.text.toString()

//            Log.d(TAG_USER, "${userData.whereIn("email", arrayListOf(targetEmail))}")
            userData.whereEqualTo("email", targetEmail).get().addOnSuccessListener { result ->
                targetId = if (result.isEmpty ){
                    Toast.makeText(this, "Wrong Email", Toast.LENGTH_SHORT).show()
                    return@addOnSuccessListener
                }else{
                    result.first().id
                }
                userData.document(targetId).get().addOnSuccessListener {
                    userData.document(targetId)
                        .update("requestList", FieldValue.arrayUnion(currentUserEmail))
                }.addOnFailureListener {
                    Toast.makeText(this, it.message, Toast.LENGTH_SHORT).show()
                }
            }
        }


        binding.buttonGet.setOnClickListener {
            val user = binding.selectedUser.text.toString()
            val tag = binding.selectedTag.text.toString()
            getSpecificArticle(articleData,user, tag)


        }
    }

    private fun getSpecificArticle(articleData:CollectionReference,user:String,tag:String){
        if (!user.isNullOrBlank()){
            if (!tag.isNullOrBlank()){
                //user+tag
                articleData.whereEqualTo("author_id",user).whereEqualTo("tag",tag).get().addOnSuccessListener {
                    for (item in it){
                        Log.d(TAG_USER,"${item.data}")
                    }
                }
            }else{
                //user
                articleData.whereEqualTo("author_id",user).get().addOnSuccessListener {
                    for (item in it){
                        Log.d(TAG_USER,"${item.data}")
                    }
                }
            }
        }else{
            //tag
            articleData.whereEqualTo("tag",tag).get().addOnSuccessListener {
                for (item in it) {
                    Log.d(TAG_USER, "${item.data}")
                }
            }
        }

    }

    private fun getTag(chipGroup: ChipGroup): String {

        // 單選
        return when (chipGroup.checkedChipId) {
            R.id.beauty -> "Beauty"
            R.id.gossiping -> "Gossiping"
            R.id.school_life -> "SchoolLife"
            else -> throw IllegalArgumentException("Unknown tag")
        }

        // 多選
//        val tagArray: ArrayList<String> = ArrayList()
//        for (id in chipGroup.checkedChipIds){
//            val tagString = when(id){
//                R.id.beauty -> "Beautiful"
//                R.id.gossiping -> "Gossiping"
//                R.id.school_life -> "SchoolLife"
//                else -> null
//            }
//            tagString?.let {
//                tagArray.add(it)
//            }
//        }
//
//        return tagArray

    }
}





