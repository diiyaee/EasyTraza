package cat.copernic.easytrazaapp.core.network

import android.content.Context

object ApiClient {

    fun getAlbaraApi(context: Context): AlbaraApiRest {
        return UsuariRetrofitInstance
            .getRetrofit(context)
            .create(AlbaraApiRest::class.java)
    }

    fun getMateriaApi(context: Context): MateriaApiRest {
        return UsuariRetrofitInstance
            .getRetrofit(context)
            .create(MateriaApiRest::class.java)
    }

    fun getProveidorApi(context: Context): ProveidorApiRest {
        return UsuariRetrofitInstance
            .getRetrofit(context)
            .create(ProveidorApiRest::class.java)
    }

    fun getLotApi(context: Context): LotApiRest {
        return UsuariRetrofitInstance
            .getRetrofit(context)
            .create(LotApiRest::class.java)
    }

    fun getUsuariApi(context: Context): UsuariApiRest {
        return UsuariRetrofitInstance
            .getRetrofit(context)
            .create(UsuariApiRest::class.java)
    }
}