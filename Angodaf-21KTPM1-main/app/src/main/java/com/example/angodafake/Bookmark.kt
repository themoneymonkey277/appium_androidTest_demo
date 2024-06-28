package com.example.angodafake

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.angodafake.Adapter.BookmarkAdapter
import com.example.angodafake.Utilities.BookmarkUtils

class Bookmark : Fragment() {
    private lateinit var bookmarksRecyclerView : RecyclerView
    private lateinit var layoutManager: RecyclerView.LayoutManager
    private lateinit var linearAdapter: BookmarkAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_bookmark, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        bookmarksRecyclerView = view.findViewById(R.id.contactsRV)
        layoutManager = LinearLayoutManager(requireContext())
        bookmarksRecyclerView.layoutManager = layoutManager
        bookmarksRecyclerView.setHasFixedSize(true)

        // Kiểm tra xem Fragment đã được gắn vào hoạt động hay chưa
        if (!isAdded) {
            return
        }

        BookmarkUtils.getAllBookmarks("tYw0x3oVS7gAd9wOdOszzvJMOEM2") { allBookmarks ->
            // Kiểm tra lại xem Fragment có vẫn còn gắn vào hoạt động không
            if (!isAdded) {
                return@getAllBookmarks
            }

            linearAdapter = BookmarkAdapter(view, requireContext(), allBookmarks)
            bookmarksRecyclerView.adapter = linearAdapter
            Log.d("allBookmarks", allBookmarks.toString())
        }
    }
}
