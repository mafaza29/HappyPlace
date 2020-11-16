package idn.faza.Adapter

import android.app.Activity
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import idn.faza.Database.DatabaseHandler
import idn.faza.happyplace.AddHappyPlaceActivity
import idn.faza.happyplace.MainActivity
import idn.faza.happyplace.R
import idn.faza.model.HappyPlacesModel
import kotlinx.android.synthetic.main.item_happy_places.view.*

open class HappyPlacesAdapter(
    private val context: Context,
    private val list: ArrayList<HappyPlacesModel>
):RecyclerView.Adapter<RecyclerView.ViewHolder>(){
    private var onClickListener: OnClickListener? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int):
            RecyclerView.ViewHolder {

        return MyViewHolder(
            LayoutInflater.from(context).inflate(
                R.layout.item_happy_places,
                parent,
                false
            )
        )
    }

    fun notifyEditItem(activity: Activity, position: Int, requestCode: Int) {
        val intent = Intent(context, AddHappyPlaceActivity::class.java)
        intent.putExtra(MainActivity.EXTRA_PLACE_DETAIL, list[position])
        activity.startActivityForResult(
            intent,
            requestCode
        )
        notifyItemChanged(position)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val data= list[position]

        if (holder is MyViewHolder) {
            holder.itemView.iv_place_image.setImageURI(Uri.parse(data.image))
            holder.itemView.tvTitle.text=data.title
            holder.itemView.tvDescription.text=data.description
        }
        holder.itemView.setOnClickListener {
            if (onClickListener!=null){
                onClickListener!!.onClick(position, data)
            }
        }
    }

    fun setOnClickListener(onClickListener: OnClickListener){
        this.onClickListener = onClickListener
    }

    interface OnClickListener{
        fun onClick(position: Int, data: HappyPlacesModel)
    }

    override fun getItemCount(): Int {
        return list.size
    }

    fun removeAt(position: Int) {
        val dbHandler = DatabaseHandler(context)
        val isDeleted = dbHandler.deleteHappyPlace(list[position])
        if (isDeleted >0) {
            list.removeAt(position)
            notifyItemRemoved(position)
        }

    }

    private class MyViewHolder (view: View): RecyclerView.ViewHolder(view)
}