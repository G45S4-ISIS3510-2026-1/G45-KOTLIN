package com.uniandes.tutorias_g45k.data.novelty

object NoveltyRepoProvider {
    fun getNoveltyRepo(): NoveltyRepository {
        //Asumiendo la posibilidad de cambios en las fuentes de datos,
        //Comprenderíamos diferentes versiones del repositorio de novedad
        //De tener mas, aqui se regularía la logica para decidir la versión
        return NoveltyRepoFirestoreImp
    }
}
