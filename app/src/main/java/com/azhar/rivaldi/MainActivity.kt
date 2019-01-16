package com.azhar.rivaldi

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.design.widget.FloatingActionButton
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.*
import kotlinx.android.synthetic.main.activity_main.*

//CREATE BY AZHAR RIVALDI, 16/01/2019

class MainActivity : AppCompatActivity() {

    private var listNotes = ArrayList<Note>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val floatingActionButton = findViewById<FloatingActionButton>(R.id.add_notes)
        floatingActionButton.setOnClickListener {
            var intent = Intent(this, NoteActivity::class.java)
            startActivity(intent)
        }

        loadQueryAll()

        lvNotes.onItemClickListener = AdapterView.OnItemClickListener { adapterView, view, position, id ->
            Toast.makeText(this, "Click on " + listNotes[position].title, Toast.LENGTH_SHORT).show()
        }
    }

    override fun onResume() {
        super.onResume()
        loadQueryAll()
    }

    fun loadQueryAll() {

        var dbManager = NoteDbManager(this)
        val cursor = dbManager.queryAll()

        listNotes.clear()
        if (cursor.moveToFirst()) {

            do {
                val id = cursor.getInt(cursor.getColumnIndex("Id"))
                val title = cursor.getString(cursor.getColumnIndex("Title"))
                val content = cursor.getString(cursor.getColumnIndex("Content"))

                listNotes.add(Note(id, title, content))

            } while (cursor.moveToNext())
        }

        var notesAdapter = NotesAdapter(this, listNotes)
        lvNotes.adapter = notesAdapter
    }

    inner class NotesAdapter : BaseAdapter {

        private var notesList = ArrayList<Note>()
        private var context: Context? = null

        constructor(context: Context, notesList: ArrayList<Note>) : super() {
            this.notesList = notesList
            this.context = context
        }

        override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View? {

            val view: View?
            val vh: ViewHolder

            if (convertView == null) {
                view = layoutInflater.inflate(R.layout.note, parent, false)
                vh = ViewHolder(view)
                view.tag = vh
                Log.i("JSA", "set Tag for ViewHolder, position: " + position)
            } else {
                view = convertView
                vh = view.tag as ViewHolder
            }

            var mNote = notesList[position]

            vh.tvTitle.text = mNote.title
            vh.tvContent.text = mNote.content

            vh.ivEdit.setOnClickListener {
                updateNote(mNote)
            }

            vh.ivDelete.setOnClickListener {
                var dbManager = NoteDbManager(this.context!!)
                val selectionArgs = arrayOf(mNote.id.toString())
                dbManager.delete("Id=?", selectionArgs)
                loadQueryAll()
            }

            return view
        }

        override fun getItem(position: Int): Any {
            return notesList[position]
        }

        override fun getItemId(position: Int): Long {
            return position.toLong()
        }

        override fun getCount(): Int {
            return notesList.size
        }
    }

    private fun updateNote(note: Note) {
        var intent = Intent(this, NoteActivity::class.java)
        intent.putExtra("MainActId", note.id)
        intent.putExtra("MainActTitle", note.title)
        intent.putExtra("MainActContent", note.content)
        startActivity(intent)
    }

    private class ViewHolder(view: View?) {
        val tvTitle: TextView
        val tvContent: TextView
        val ivEdit: ImageView
        val ivDelete: ImageView

        init {
            this.tvTitle = view?.findViewById(R.id.tvTitle) as TextView
            this.tvContent = view?.findViewById(R.id.tvContent) as TextView
            this.ivEdit = view?.findViewById(R.id.ivEdit) as ImageView
            this.ivDelete = view?.findViewById(R.id.ivDelete) as ImageView
        }
    }

}