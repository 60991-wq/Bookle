package be.esi.prj.bookle.mvp.model.dataBaseRepository;

import be.esi.prj.bookle.mvp.model.dataAccessObject.WordKeyDao;
import be.esi.prj.bookle.mvp.model.dto.WordKey;

import java.util.List;
import java.util.Optional;

public class WordKeyRepository implements Repository<WordKey, WordKey> {

    private final WordKeyDao wordKeyDao;

    public WordKeyRepository(WordKeyDao wordKeyDao) {
        this.wordKeyDao = wordKeyDao;
    }


    @Override
    public List<WordKey> findAll() throws RepositoryException {
        return wordKeyDao.findAll();
    }

    @Override
    public WordKey save(WordKey item) throws RepositoryException {
        return wordKeyDao.save(item);
    }

    public List<WordKey> findByTitleAndPage(String title, Optional<Integer> page) throws RepositoryException {
        return wordKeyDao.findByTitleAndPage(title, page);
    }

}
