class SearchbirdServiceImpl(index: Index) extends thrift.SearchbirdService.FutureIface {
  def get(key: String) = index.get(key)
  def put(key: String, value: String) =
    index.put(key, value) map { _ => null: java.lang.Void }
  def search(query: String) = index.search(query)
}