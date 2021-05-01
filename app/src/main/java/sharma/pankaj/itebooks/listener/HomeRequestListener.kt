package sharma.pankaj.itebooks.listener

import sharma.pankaj.itebooks.data.db.entities.Data

interface HomeRequestListener {
    fun onMessage(msg: String)
    fun onStartRequest()
    fun onStopRequest()
    fun onHomeResponse(data: List<Data>)
    fun onHomeResponse(data: sharma.pankaj.itebooks.data.network.responses.Data)
}