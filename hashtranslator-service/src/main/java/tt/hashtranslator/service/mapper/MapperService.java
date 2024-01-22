package tt.hashtranslator.service.mapper;

import tt.hashtranslator.exception.MapperException;

public interface MapperService<Entity, Dto> {
    Entity dtoToEntity(Dto dto) throws MapperException;
}
